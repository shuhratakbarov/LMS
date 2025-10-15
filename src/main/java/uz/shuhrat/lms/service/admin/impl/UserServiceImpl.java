package uz.shuhrat.lms.service.admin.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.repository.student.StudentRoleRepository;
import uz.shuhrat.lms.db.repository.teacher.TeacherRepository;
import uz.shuhrat.lms.dto.response.GroupDataResponseDto;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.dto.response.UserProfileDetailsResponseDto;
import uz.shuhrat.lms.projection.CourseGroupCountProjection;
import uz.shuhrat.lms.projection.UserSummaryProjection;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.*;
import uz.shuhrat.lms.dto.request.UserRequestDto;
import uz.shuhrat.lms.dto.response.UserResponseDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.admin.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRoleRepository studentRoleRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public GeneralResponseDto<?> currentUserInfo() {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                throw new Exception("Current user doesn't exist");
            }

            int courseCount = 0;
            int groupCount = 0;
            if (currentUser.getRole().equals(Role.STUDENT)) {
                CourseGroupCountProjection result = studentRoleRepository.getStudentCourseAndGroupCount(currentUser.getId());
                courseCount = result.getCourseCount().intValue();
                groupCount  = result.getGroupCount().intValue();
            } else if (currentUser.getRole().equals(Role.TEACHER)) {
                CourseGroupCountProjection result = teacherRepository.getTeacherCourseAndGroupCount(currentUser.getId());
                courseCount = result.getCourseCount().intValue();
                groupCount  = result.getGroupCount().intValue();
            }

            UserProfileDetailsResponseDto userProfileDetailsResponseDto = new UserProfileDetailsResponseDto(
                    currentUser.getFirstName(),
                    currentUser.getLastName(),
                    currentUser.getRole().toString(),
                    currentUser.getUsername(),
                    currentUser.getEmail(),
                    currentUser.getPhone(),
                    currentUser.getAddress(),
                    currentUser.getBirthDate(),
                    currentUser.getLastSeen(),
                    courseCount,
                    groupCount
            );

            return new GeneralResponseDto<>(true, "ok", userProfileDetailsResponseDto);
        } catch (Exception e) {
            System.err.println("User Service currentUserInfo method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage(), null);
        }
    }

    public User getById(UUID userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));
    }

    @Override
    public GeneralResponseDto<?> save(UserRequestDto userForm) throws Exception {
        try {
            User user = User.builder()
                    .firstName(userForm.firstName())
                    .lastName(userForm.lastName())
                    .email(userForm.email())
                    .phone(userForm.phone())
                    .address(userForm.address())
                    .birthDate(userForm.birthDate())
                    .username(userForm.username())
                    .password(passwordEncoder.encode(userForm.password()))
                    .build();

            if (Role.isRoleValid(userForm.role())) user.setRole(Role.valueOf(userForm.role().toUpperCase()));
            else throw new Exception("Invalid role");
            userRepository.save(user);
            return new GeneralResponseDto<>(true, "Ok");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> edit(UUID id, UserRequestDto userForm) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return new GeneralResponseDto<>(false, "User not found");
            }
            User user = userOptional.get();
            user.setFirstName(userForm.firstName());
            user.setLastName(userForm.lastName());
            user.setEmail(userForm.email());
            user.setPhone(userForm.phone());
            user.setAddress(userForm.address());
            user.setBirthDate(userForm.birthDate());
            user.setUsername(userForm.username());
            userRepository.save(user);
            return new GeneralResponseDto<>(true, "Ok");
        } catch (Exception e) {
            System.out.println("User Service edit method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> delete(UUID id) throws Exception {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) return new GeneralResponseDto<>(false, "No such user");
            User user = userOptional.get();
            user.setActive(false);
            userRepository.save(user);
            return new GeneralResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("User Service delete method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> findTeachersToSelect() {
        return new GeneralResponseDto<>(true, "ok", userRepository.findTeachersForSelect());
    }

    @Override
    public GeneralResponseDto<?> getUserList(String role, String searching, int page, int size) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                return new GeneralResponseDto<>(false, "Current user is null");
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<User> pages;
            if (Role.isRoleValid(role)) {
                pages = userRepository.getUserList(searching, Role.valueOf(role), pageable);
            } else {
                return new GeneralResponseDto<>(false, "role not found", null);
            }
            return new GeneralResponseDto<>(true, "Ok", pages);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> getStudentsOfGroup(Long groupId, int page, int size) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (optionalGroup.isEmpty()) {
                return new GeneralResponseDto<>(false, "Group doesn't exist!");
            }
            if (currentUser == null) {
                return new GeneralResponseDto<>(false, "Current user is null");
            }
            if (currentUser.getRole() == Role.ADMIN) {
                Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
                Page<UserSummaryProjection> pages = userRepository.getStudentsOfGroup(groupId, pageable);
                return new GeneralResponseDto<>(true, "Ok", new GroupDataResponseDto(pages, optionalGroup.get()));
            }
            return new GeneralResponseDto<>(false, "Access denied!!!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> searchStudent(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent() && optionalUser.get().getRole() == Role.STUDENT) {
            return new GeneralResponseDto<>(true, "ok", optionalUser.get());
        }
        return new GeneralResponseDto<>(false, "No such student!");
    }

    @Override
    public Instant updateLastSeen(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    Instant now = Instant.now();
                    user.setLastSeen(now);
                    userRepository.save(user);
                    return now;
                })
                .orElse(null);
    }

}
