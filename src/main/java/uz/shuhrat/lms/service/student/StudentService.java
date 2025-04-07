package uz.shuhrat.lms.service.student;

import uz.shuhrat.lms.dto.ResponseDto;

public interface StudentService {

    ResponseDto<?> getHomeworksOfStudent(Long groupId, int page, int size);

    ResponseDto<?> getHomeworkCount();

    ResponseDto<?> getStudentGroupList(String keyword, int page, int size);
}
