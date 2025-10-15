package uz.shuhrat.lms.service.student;

import uz.shuhrat.lms.dto.GeneralResponseDto;

public interface StudentService {

    GeneralResponseDto<?> getHomeworksOfStudent(Long groupId, int page, int size) throws Exception;

    GeneralResponseDto<?> getHomeworkNotification();

    GeneralResponseDto<?> getStudentGroupList(String keyword, int page, int size) throws Exception;
}
