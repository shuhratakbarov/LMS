package uz.farobiy.lesson_11_backend.service.student.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.farobiy.lesson_11_backend.db.domain.User;
import uz.farobiy.lesson_11_backend.db.repository.student.StudentRoleRepository;
import uz.farobiy.lesson_11_backend.dto.ResponseDto;
import uz.farobiy.lesson_11_backend.helper.SecurityHelper;
import uz.farobiy.lesson_11_backend.service.student.StudentService;

import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRoleRepository studentRoleRepository;

    @Override
    public ResponseDto getGroupsOfStudent() {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null &&
                currentUser.getRole().getName().equals("ROLE_STUDENT") &&
                currentUser.isActive() ) {
            return new ResponseDto<>(true, "ok", studentRoleRepository.getGroupsOfStudent(currentUser.getId()));
        } else {
            return new ResponseDto<>(false, "Sizga ruxsat berilmagan");
        }

    }

    @Override
    public ResponseDto getHomeworksOfStudent(Long groupId) {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.isActive()) {
            return new ResponseDto<>(true, "ok", studentRoleRepository.getHomeworksOfStudent(groupId, currentUser.getId()));
        } else {
            return new ResponseDto<>(false, "Urinma foydasi yo'q!");
        }
    }

    @Override
    public ResponseDto getHomeworkCount() {
        User student = SecurityHelper.getCurrentUser();
        if (student != null) {
            return new ResponseDto<>(true, "Bajarilmagan topshiriqlar soni", studentRoleRepository.getHomeworkCount(student.getId()));
        } else {
            return new ResponseDto<>(false, "Error occurred");
        }
    }

}
