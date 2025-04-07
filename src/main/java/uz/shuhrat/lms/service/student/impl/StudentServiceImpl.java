package uz.shuhrat.lms.service.student.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.customDto.student.StudentDto;
import uz.shuhrat.lms.db.customDto.student.StudentHomeworkDto;
import uz.shuhrat.lms.db.customDto.teacher.GroupCustomForTeacher;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.student.StudentRoleRepository;
import uz.shuhrat.lms.dto.ResponseDto;
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
    public ResponseDto<?> getStudentGroupList(String keyword, int page, int size) {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.isActive() && currentUser.getRole().getName().equals("ROLE_STUDENT")) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<StudentDto> studentDtoPage = studentRoleRepository.getGroupsOfStudent(currentUser.getId(), keyword, pageable);
            return new ResponseDto<>(true, "ok", studentDtoPage);
        }
        return new ResponseDto<>(false, "Group ro'yxatini olishga ruxsat yo'q!!!");
    }

    @Override
    public ResponseDto<?> getHomeworksOfStudent(Long groupId, int page, int size) {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.isActive() && currentUser.getRole().getName().equals("ROLE_STUDENT")) {
            Pageable pageable = PageRequest.of(page, size);
            Page<StudentHomeworkDto> studentHomeworkDtoPage = studentRoleRepository.getHomeworksOfStudent(groupId, currentUser.getId(), pageable);
            return new ResponseDto<>(true, "ok", studentHomeworkDtoPage);
        } else {
            return new ResponseDto<>(false, "Urinma foydasi yo'q!");
        }
    }

    @Override
    public ResponseDto<?> getHomeworkCount() {
        User student = SecurityHelper.getCurrentUser();
        if (student != null) {
            return new ResponseDto<>(true, "Bajarilmagan topshiriqlar soni", studentRoleRepository.getHomeworkCount(student.getId()));
        } else {
            return new ResponseDto<>(false, "Error occurred");
        }
    }
}
