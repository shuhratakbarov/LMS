package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.CreateCourseForm;

public interface CourseService {
    ResponseDto<?> save(CreateCourseForm form);

    ResponseDto<?> edit(Long id, CreateCourseForm form);

    ResponseDto<?> delete(Long id);

    ResponseDto<?> findCoursesForSelect();

    ResponseDto<?> getCourseList(String searching, int page, int size) throws Exception;
}
