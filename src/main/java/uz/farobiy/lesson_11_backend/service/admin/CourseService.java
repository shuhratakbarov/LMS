package uz.farobiy.lesson_11_backend.service.admin;


import uz.farobiy.lesson_11_backend.db.domain.Course;
import uz.farobiy.lesson_11_backend.dto.ResponseDto;
import uz.farobiy.lesson_11_backend.dto.form.CreateCourseForm;

public interface CourseService {
    ResponseDto save(CreateCourseForm form);
    ResponseDto edit(Long id,CreateCourseForm form);
    ResponseDto delete(Long id);
    ResponseDto findAll(int page, int size);
    ResponseDto findCoursesForSelect();
    ResponseDto search(String searching, int page, int size) throws Exception;

}
