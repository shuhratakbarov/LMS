package uz.farobiy.lms.service.student;

import uz.farobiy.lms.dto.ResponseDto;

public interface StudentService {
    ResponseDto<?> getGroupsOfStudent();

    ResponseDto<?> getHomeworksOfStudent(Long groupId);

    ResponseDto<?> getHomeworkCount();
}
