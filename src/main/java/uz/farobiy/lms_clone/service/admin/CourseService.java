package uz.farobiy.lms_clone.service.admin;


import uz.farobiy.lms_clone.dto.ResponseDto;
import uz.farobiy.lms_clone.dto.form.CreateCourseForm;

public interface CourseService {
    ResponseDto save(CreateCourseForm form);
    ResponseDto edit(Long id,CreateCourseForm form);
    ResponseDto delete(Long id);
    ResponseDto findAll(int page, int size);
    ResponseDto findCoursesForSelect();
    ResponseDto search(String searching, int page, int size) throws Exception;

}
