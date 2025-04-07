package uz.shuhrat.lms.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.component.jwt.JwtService;
import uz.shuhrat.lms.db.customDto.admin.GroupDataDto;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.Role;
import uz.shuhrat.lms.db.domain.TokenBlacklist;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.customDto.admin.UserCustomDtoForAdmin;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.RoleRepository;
import uz.shuhrat.lms.db.repository.admin.TokenBlacklistRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.*;
import uz.shuhrat.lms.dto.form.LoginForm;
import uz.shuhrat.lms.dto.form.user.CreateUserForm;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.admin.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, TokenBlacklistRepository tokenBlacklistRepository, RoleRepository roleRepository, UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseDto<?> login(LoginForm form) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword()));
            if (authentication.isAuthenticated()) {
                User user = (User) authentication.getPrincipal();
                String accessToken = jwtService.generateAccessToken(form.getUsername());
                String refreshToken = jwtService.generateRefreshToken(form.getUsername());
                return ResponseDto.success(
                        "Login successful",
                        LoginResponseDto.builder()
                                .user(new UserResponseDto(user.getFirstName(), user.getLastName(), user.getRole().getName(), 0))
                                .access_token(accessToken)
                                .refresh_token(refreshToken)
                                .accessExpiration(System.currentTimeMillis() + accessExpiration)
                                .build());
            }
            return ResponseDto.error("Invalid credentials");
        } catch (Exception e) {
            return ResponseDto.error("Login failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> logout(String token) {
        try {
            if (token == null || jwtService.isTokenExpired(token)) {
                return ResponseDto.error("Invalid or expired token");
            }
            if (tokenBlacklistRepository.findByToken(token).isPresent()) {
                return ResponseDto.error("Token already logged out");
            }
            Instant expiration = jwtService.extractExpiration(token);
            tokenBlacklistRepository.save(new TokenBlacklist(token, expiration));
            return ResponseDto.success("Logout successful", null);
        } catch (Exception e) {
            return ResponseDto.error("Logout failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> refresh(String refreshToken) {
        try {
            if (refreshToken == null || jwtService.isTokenExpired(refreshToken)) {
                return ResponseDto.error("Invalid or expired refresh token");
            }
            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new Exception("User not found"));
            String newAccessToken = jwtService.generateAccessToken(username);
            return ResponseDto.success(
                    "Token refreshed",
                    LoginResponseDto.builder()
                            .user(new UserResponseDto(user.getFirstName(), user.getLastName(), user.getRole().getName(), 0))
                            .access_token(newAccessToken)
                            .refresh_token(refreshToken)
                            .accessExpiration(System.currentTimeMillis() + accessExpiration)
                            .build());
        } catch (Exception e) {
            return ResponseDto.error("Refresh failed: " + e.getMessage());
        }
    }
    @Override
    public ResponseDto<?> currentUserInfo() {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                throw new Exception("Current user doesn't exist");
            }
            int notificationCount = 0;
            UserResponseDto dto = new UserResponseDto(currentUser.getFirstName(), currentUser.getLastName(), currentUser.getRole().getName(), notificationCount);
            return new ResponseDto<>(true, "ok", dto);
        } catch (Exception e) {
            System.err.println("User Service currentUserInfo method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto<?> save(CreateUserForm userForm) throws Exception {
        try {
            User user = User.builder()
                    .firstName(userForm.getFirstName())
                    .lastName(userForm.getLastName())
                    .email(userForm.getEmail())
                    .phone(userForm.getPhone())
                    .address(userForm.getAddress())
                    .birthDate(userForm.getBirthDate())
                    .username(userForm.getUsername())
                    .password(passwordEncoder.encode(userForm.getPassword()))
                    .build();
            Optional<Role> role = roleRepository.findById(userForm.getRoleId());
            if (role.isPresent()) user.setRole(role.get());
            else throw new Exception("Role topilmadi");
            if (userForm.getGroups() != null) {
                List<Long> groups = userForm.getGroups();
                Optional<Long> count = groupRepository.getCountByIds(groups);
                if (count.isPresent() && count.get() != groups.size()) {
                    return new ResponseDto<>(false, "Mavjud bo'lmagan guruh(lar) mavjud");
                } else {
                    user = userRepository.save(user);
                    if (user.getRole().getId() == 2) {
                        groupRepository.associateTeacherToGroups(groups, user.getId());
                    } else {
                        groupRepository.addStudentToGroups(groups, user.getId());
                    }
                }
            } else {
                userRepository.save(user);
            }
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> edit(UUID id, CreateUserForm userForm) throws Exception {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return new ResponseDto<>(false, "User not found");
            }
            User user = userOptional.get();
            user.setFirstName(userForm.getFirstName());
            user.setLastName(userForm.getLastName());
            user.setEmail(userForm.getEmail());
            user.setPhone(userForm.getPhone());
            user.setAddress(userForm.getAddress());
//            user.setBirthDate(userForm.getBirthDate());
            user.setUsername(userForm.getUsername());
            if (userForm.getUsername() != null) {
                user.setPassword(passwordEncoder.encode(userForm.getPassword()));
            }
            Optional<Role> rOp = roleRepository.findById(userForm.getRoleId());
            if (rOp.isPresent()) user.setRole(rOp.get());
            else throw new Exception("role not found");
            if (userForm.getGroups() != null) {
                List<Long> groups = userForm.getGroups();
                Optional<Long> count = groupRepository.getCountByIds(groups);
                if (count.isPresent() && count.get() != groups.size()) {
                    return new ResponseDto<>(false, "Mavjud bo'lmagan guruh(lar) mavjud");
                }
                groupRepository.addStudentToGroups(groups, user.getId());
            } else {
                user = userRepository.save(user);
            }
            if (user.getId() == null) throw new Exception("User saqlanmadi");
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            System.out.println("User Service edit method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> delete(UUID id) throws Exception {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return new ResponseDto<>(false, "Bunday user mavjud emas");
            }
            User user = userOptional.get();
            user.setActive(false);
            userRepository.save(user);
            return new ResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("User Service delete method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> findTeachersForSelect() {
        return new ResponseDto<>(true, "ok", userRepository.findTeachersForSelect());
    }

    @Override
    public ResponseDto<?> getUserList(String role, String searching, int page, int size) throws Exception {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                return new ResponseDto<>(false, "Current user is null");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<User> pages;
            Long roleId = null;
            if (role.equals("student")) {
                roleId = 3L;
            } else if (role.equals("teacher")) {
                roleId = 2L;
            }
            if (roleId != null) {
                pages = userRepository.getUserList(searching, roleId, pageable);
            } else {
                return new ResponseDto<>(false, "role not found", null);
            }
            PageDataResponseDto<Page<User>> dto = new PageDataResponseDto<>(pages, pages.getTotalElements());
            return new ResponseDto<>(true, "Ok", dto);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> getStudentsOfGroup(Long groupId, int page, int size) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (optionalGroup.isEmpty()) {
                return new ResponseDto<>(false, "Group Mavjud emas!!!");
            }
            if (currentUser == null) {
                return new ResponseDto<>(false, "Current user is null");
            }
            if (currentUser.getRole().getId() == 1) {
                Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
                Page<UserCustomDtoForAdmin> pages = userRepository.getStudentsOfGroup(groupId, pageable);
                PageDataResponseDto<Page<UserCustomDtoForAdmin>> dto = new PageDataResponseDto<>(pages, pages.getTotalElements());
                return new ResponseDto<>(true, "Ok", new GroupDataDto(dto, optionalGroup.get()));
            }
            return new ResponseDto<>(false, "Ruxsat mavjud emas!!!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> searchStudent(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent() && optionalUser.get().getRole().getId() == 3) {
            return new ResponseDto<>(true, "ok", optionalUser.get());
        }
        return new ResponseDto<>(false, "Bunday student mavjud emas!!!");
    }
}
