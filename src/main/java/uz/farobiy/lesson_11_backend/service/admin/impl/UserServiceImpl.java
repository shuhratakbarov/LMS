package uz.farobiy.lesson_11_backend.service.admin.impl;

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
import uz.farobiy.lesson_11_backend.component.jwt.JwtService;
import uz.farobiy.lesson_11_backend.db.domain.Group;
import uz.farobiy.lesson_11_backend.db.domain.Role;
import uz.farobiy.lesson_11_backend.db.domain.User;
import uz.farobiy.lesson_11_backend.db.domain.customDto.admin.UserCustomDtoForAdmin;
import uz.farobiy.lesson_11_backend.db.repository.admin.GroupRepository;
import uz.farobiy.lesson_11_backend.db.repository.RoleRepository;
import uz.farobiy.lesson_11_backend.db.repository.admin.UserRepository;
import uz.farobiy.lesson_11_backend.dto.*;
import uz.farobiy.lesson_11_backend.dto.form.LoginForm;
import uz.farobiy.lesson_11_backend.dto.form.user.CreateUserForm;
import uz.farobiy.lesson_11_backend.helper.SecurityHelper;
import uz.farobiy.lesson_11_backend.service.admin.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.expireDate}")
    private Long jwtExpireDate;

    @Override
    public ResponseDto signin(LoginForm form) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(form.getUsername());
                return new ResponseDto(
                        true, "Ok", LoginResponseDto.builder()
                        .access_token(token)
                        .refresh_token(token)
                        .expireDate(jwtExpireDate)
                        .build()
                );
            } else {
                throw new Exception("invalid user request..!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto currentUserInfo() throws Exception {
        try {
//            BigDecimal a=BigDecimal.valueOf(54785422115.2);
//            BigDecimal b=BigDecimal.valueOf(547855.2);
//            System.out.println(a.subtract(b));
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                throw new Exception("Current user is null");
            }
            String roleName = currentUser.getRole().getName();

            UserResponseDto dto = new UserResponseDto(currentUser.getFirstName(), currentUser.getLastName(), roleName);
            return new ResponseDto(true, "ok", dto);
        } catch (Exception e) {
            System.err.println(e);
            return new ResponseDto(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto save(CreateUserForm userForm) throws Exception {
        try {
            User user = new User();
            user.setFirstName(userForm.getFirstName());
            user.setLastName(userForm.getLastName());
            user.setEmail(userForm.getEmail());
            user.setPhone(userForm.getPhone());
            user.setAddress(userForm.getAddress());
            user.setBirthDate(userForm.getBirthDate());
            user.setUsername(userForm.getUsername());
            user.setPassword(passwordEncoder.encode(userForm.getPassword()));
            Optional<Role> rOp = roleRepository.findById(userForm.getRoleId());
            if (rOp.isPresent()) user.setRole(rOp.get());
            else throw new Exception("Role topilmadi");

            if (userForm.getGroups() != null) {
                List<Long> groups = userForm.getGroups();
                Optional<Long> count = groupRepository.getCountByIds(groups);
                if (count.isPresent() && count.get() != groups.size()) {
                    return new ResponseDto<>(false, "Mavjud bo'lmagan guruh(lar) mavjud");
                }
                user = userRepository.save(user);
                groupRepository.addStudentToGroups(groups, user.getId());
            } else {
                user = userRepository.save(user);
            }
            if (user == null) throw new Exception("User nimagadir saqlanmadi");
            return new ResponseDto(true, "Ok");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto edit(UUID id, CreateUserForm userForm) throws Exception {
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
            if (user == null) throw new Exception("User nimagadir saqlanmadi");
            return new ResponseDto(true, "Ok");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto delete(UUID id) throws Exception {
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
            e.printStackTrace();
            return new ResponseDto<>(false, e.getMessage());
        }
    }


    @Override
    public ResponseDto findAllByRoleId(Long roleId, String isActive, int page, int size) throws Exception {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<User> pages;
            if (isActive.equals("true")) {
                pages = userRepository.findAllByRoleIdAndActive(roleId, pageable);
            } else if (isActive.equals("false")) {
                pages = userRepository.findAllByRoleIdAndNotActive(roleId, pageable);
            } else {
                pages = userRepository.findAllByRoleId(roleId, pageable);
            }
            PageDataResponseDto<Page<User>> dto = new PageDataResponseDto<>(pages, pages.getTotalElements());
            return new ResponseDto(true, "Ok", dto);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto findTeachersForSelect() {
        return new ResponseDto<>(true, "ok", userRepository.findTeachersForSelect());
    }


    @Override
    public ResponseDto search(Long roleId, String isActive, String searching, int page, int size) throws Exception {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                return new ResponseDto<>(false, "Current user is null");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<User> pages;
            if (roleId != null) {
                if (isActive.equals("true")) {
                    pages = userRepository.searchInRoleId(searching, roleId, true, pageable);
                } else if (isActive.equals("false")) {
                    pages = userRepository.searchInRoleId(searching, roleId, false, pageable);
                } else {
                    pages = userRepository.searchInRoleIdAll(searching, roleId, pageable);
                }

            } else {
                return new ResponseDto<>(false, "role not found", null);
            }
            PageDataResponseDto<Page<User>> dto = new PageDataResponseDto<>(pages, pages.getTotalElements());
            return new ResponseDto(true, "Ok", dto);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto getStudentsOfGroup(Long groupId, int page, int size) throws Exception {
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
                return new ResponseDto(true, "Ok", dto);
            }
            return new ResponseDto<>(false, "Ruxsat mavjud emas!!!");


        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseDto(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto searchStudent(String username) throws Exception {

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent() && optionalUser.get().getRole().getId() == 3) {
            return new ResponseDto<>(true, "ok", optionalUser.get());
        }
        return new ResponseDto<>(false, "Bunday student mavjud emas!!!");
    }

}
