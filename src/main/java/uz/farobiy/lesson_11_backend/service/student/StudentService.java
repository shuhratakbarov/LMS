package uz.farobiy.lesson_11_backend.service.student;

import uz.farobiy.lesson_11_backend.dto.ResponseDto;

import java.util.UUID;

public interface StudentService {
    ResponseDto getGroupsOfStudent();
    ResponseDto getHomeworksOfStudent(Long groupId);

    ResponseDto getHomeworkCount();

}
