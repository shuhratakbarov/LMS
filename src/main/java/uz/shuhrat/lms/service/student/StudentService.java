package uz.shuhrat.lms.service.student;

import uz.shuhrat.lms.dto.ResponseDto;

public interface StudentService {
    ResponseDto<?> getGroupsOfStudent();

    ResponseDto<?> getHomeworksOfStudent(Long groupId);

    ResponseDto<?> getHomeworkCount();
}
