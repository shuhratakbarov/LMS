package uz.farobiy.lms_clone.service.student;

import uz.farobiy.lms_clone.dto.ResponseDto;

public interface StudentService {
    ResponseDto getGroupsOfStudent();

    ResponseDto getHomeworksOfStudent(Long groupId);

    ResponseDto getHomeworkCount();

}
