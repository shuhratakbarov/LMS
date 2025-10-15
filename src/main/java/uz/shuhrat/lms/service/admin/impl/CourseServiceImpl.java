package uz.shuhrat.lms.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.domain.Course;
import uz.shuhrat.lms.db.repository.admin.CourseRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.CourseRequestDto;
import uz.shuhrat.lms.service.admin.CourseService;

import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public GeneralResponseDto<?> save(CourseRequestDto form) {
        try {
            Course course = new Course();
            course.setName(form.name());
            course.setDuration(form.duration());
            course.setDescription(form.description());
            courseRepository.save(course);
            return new GeneralResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("Course Service save method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> getCourseList(String searching, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Course> pages = courseRepository.getCourseList(searching, pageable);
        return new GeneralResponseDto<>(true, "ok", pages);
    }

    @Override
    public GeneralResponseDto<?> edit(Long id, CourseRequestDto form) {
        try {
            Optional<Course> cOp = courseRepository.findById(id);
            if (cOp.isEmpty()) {
                return new GeneralResponseDto<>(false, "Course not found");
            }
            Course course = cOp.get();
            course.setName(form.name());
            course.setDuration(form.duration());
            course.setDescription(form.description());
            courseRepository.save(course);
            return new GeneralResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("Course Service edit method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> delete(Long id) {
        try {
            Optional<Course> course = courseRepository.findById(id);
            if (course.isEmpty()) {
                return new GeneralResponseDto<>(false, "Course topilmadi!!!");
            }
            Optional<Long> count = courseRepository.countGroupsByCourseId(id);
            if (count.isPresent()) {
                if (count.get() > 0) {
                    return new GeneralResponseDto<>(false, "Bu kursda guruhlar mavjud!!!");
                }
            } else {
                return new GeneralResponseDto<>(false, "Count is not present");
            }
            courseRepository.deleteById(id);
            return new GeneralResponseDto<>(true, "O'chirildi");
        } catch (Exception e) {
            System.out.println("Course Service delete method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> findCoursesForSelect() {
        return new GeneralResponseDto<>(true, "ok", courseRepository.findCoursesForSelect());
    }
}
