package uz.farobiy.lms_clone.service.teacher;

import uz.farobiy.lms_clone.dto.ResponseDto;
import uz.farobiy.lms_clone.dto.form.EvaluateHomework;

import java.util.UUID;


public interface TeacherService  {
    ResponseDto getGroups(int page,int size) throws Exception;
    ResponseDto getGroupsByCourseId(String courseId, int page, int size) throws Exception;

    ResponseDto getStudentOfGroup(Long groupId, String taskId, int page, int size) throws Exception;

    ResponseDto evaluateHomework(UUID homeworkId, EvaluateHomework homework);


}
