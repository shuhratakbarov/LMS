package uz.shuhrat.lms.service.teacher;

import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.EvaluateHomework;

import java.util.UUID;

public interface TeacherService {
    ResponseDto<?> getGroups(String keyword, int page, int size);

    ResponseDto<?> getHomeworkList(Long groupId, String taskId, int page, int size) throws Exception;

    ResponseDto<?> evaluateHomework(UUID homeworkId, EvaluateHomework homework);
}
