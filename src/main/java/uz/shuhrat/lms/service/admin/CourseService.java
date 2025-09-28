package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.CourseRequestDto;

public interface CourseService {
    GeneralResponseDto<?> save(CourseRequestDto form);

    GeneralResponseDto<?> edit(Long id, CourseRequestDto form);

    GeneralResponseDto<?> delete(Long id);

    GeneralResponseDto<?> findCoursesForSelect();

    GeneralResponseDto<?> getCourseList(String searching, int page, int size) throws Exception;
}
