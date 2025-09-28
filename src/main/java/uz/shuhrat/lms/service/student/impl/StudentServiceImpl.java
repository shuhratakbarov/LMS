package uz.shuhrat.lms.service.student.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.projection.GroupsOfStudentProjection;
import uz.shuhrat.lms.projection.StudentHomeworkProjection;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.db.repository.student.StudentRoleRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.student.StudentService;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRoleRepository studentRoleRepository;

    @Autowired
    public StudentServiceImpl(StudentRoleRepository studentRoleRepository) {
        this.studentRoleRepository = studentRoleRepository;
    }

    @Override
    public GeneralResponseDto<?> getStudentGroupList(String keyword, int page, int size) throws Exception {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Authentifikatsiyadan o'ting!");
        }
        if (currentUser.isActive() && currentUser.getRole() == Role.STUDENT) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<GroupsOfStudentProjection> studentDtoPage = studentRoleRepository.getGroupsOfStudent(currentUser.getId(), keyword, pageable);
            return new GeneralResponseDto<>(true, "ok", studentDtoPage);
        }
        return new GeneralResponseDto<>(false, "Group ro'yxatini olishga ruxsat yo'q!!!");
    }

    @Override
    public GeneralResponseDto<?> getHomeworksOfStudent(Long groupId, int page, int size) throws Exception {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Authentifikatsiyadan o'ting!");
        }
        if (currentUser.isActive() && currentUser.getRole() == Role.STUDENT) {
            Pageable pageable = PageRequest.of(page, size);
            Page<StudentHomeworkProjection> studentHomeworkDtoPage = studentRoleRepository.getHomeworksOfStudent(groupId, currentUser.getId(), pageable);
            return new GeneralResponseDto<>(true, "ok", studentHomeworkDtoPage);
        } else {
            return new GeneralResponseDto<>(false, "You are not allowed");
        }
    }

    @Override
    public GeneralResponseDto<?> getHomeworkNotification() {
        User student = SecurityHelper.getCurrentUser();
        if (student != null) {
            return new GeneralResponseDto<>(true, "Bajarilmagan topshiriqlar", studentRoleRepository.getHomeworkNotificationDetails(student.getId()));
        } else {
            return new GeneralResponseDto<>(false, "Error occurred");
        }
    }
}
