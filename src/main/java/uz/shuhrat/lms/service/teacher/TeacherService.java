package uz.shuhrat.lms.service.teacher;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.EvaluateHomeworkRequestDto;

import java.util.UUID;

public interface TeacherService {
    GeneralResponseDto<?> getGroups(String keyword, int page, int size) throws Exception;

    GeneralResponseDto<?> getHomeworkList(Long groupId, String taskId, int page, int size) throws Exception;

    GeneralResponseDto<?> evaluateHomework(UUID homeworkId, EvaluateHomeworkRequestDto homework);

    GeneralResponseDto<?> findCoursesForSelect();
}
