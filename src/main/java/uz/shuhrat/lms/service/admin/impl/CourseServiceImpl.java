package uz.shuhrat.lms.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.domain.Course;
import uz.shuhrat.lms.db.repository.admin.CourseRepository;
import uz.shuhrat.lms.dto.PageDataResponseDto;
import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.CreateCourseForm;
import uz.shuhrat.lms.service.admin.CourseService;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public ResponseDto<?> save(CreateCourseForm form) {
        try {
            if (form == null || form.getName() == null || form.getDescription() == null) {
                throw new Exception("Ma'lumotlar to'liq emas!!!");
            }
            Course course = new Course();
            course.setName(form.getName());
            course.setDuration(form.getDuration());
            course.setDescription(form.getDescription());
            course = courseRepository.save(course);
            if (course.getId() == null) {
                return new ResponseDto<>(false, "Bazaga saqalanmadi");
            }
            return new ResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("Course Service save method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> edit(Long id, CreateCourseForm form) {
        try {
            Optional<Course> cOp = courseRepository.findById(id);
            if (cOp.isEmpty()) {
                return new ResponseDto<>(false, "Course not found");
            }
            Course course = cOp.get();
            course.setName(form.getName());
            course.setDuration(form.getDuration());
            course.setDescription(form.getDescription());
            course = courseRepository.save(course);
            if (course.getId() == null) {
                return new ResponseDto<>(false, "Bazaga saqalanmadi");
            }
            return new ResponseDto<>(true, "ok");
        } catch (Exception e) {
            System.out.println("Course Service edit method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> delete(Long id) {
        try {
            Optional<Course> course = courseRepository.findById(id);
            if (course.isEmpty()) {
                return new ResponseDto<>(false, "Course topilmadi!!!");
            }
            Optional<Long> count = courseRepository.countGroupsByCourseId(id);
            if (count.isPresent()) {
                if (count.get() > 0) {
                    return new ResponseDto<>(false, "Bu kursda guruhlar mavjud!!!");
                }
            } else {
                return new ResponseDto<>(false, "Count is not present");
            }
            courseRepository.deleteById(id);
            return new ResponseDto<>(true, "O'chirildi");
        } catch (Exception e) {
            System.out.println("Course Service delete method: " + e.getMessage());
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<?> findCoursesForSelect() {
        return new ResponseDto<>(true, "ok", courseRepository.findCoursesForSelect());
    }

    @Override
    public ResponseDto<?> getCourseList(String searching, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Course> pages = courseRepository.getCourseList(searching, pageable);
        List<Course> list = pages.getContent();
        PageDataResponseDto<List<Course>> dto = new PageDataResponseDto<>(list, pages.getTotalElements());
        return new ResponseDto<>(true, "ok", dto);
    }
}
