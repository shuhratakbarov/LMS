package uz.farobiy.lesson_11_backend.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.farobiy.lesson_11_backend.db.domain.Course;
import uz.farobiy.lesson_11_backend.db.repository.admin.CourseRepository;
import uz.farobiy.lesson_11_backend.dto.PageDataResponseDto;
import uz.farobiy.lesson_11_backend.dto.ResponseDto;
import uz.farobiy.lesson_11_backend.dto.form.CreateCourseForm;
import uz.farobiy.lesson_11_backend.service.admin.CourseService;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public ResponseDto save(CreateCourseForm form) {
        try {
            if (form == null || form.getName() == null || form.getDescription() == null) {
                throw new Exception("Ma'lumotlar to'liq emas!!!");
            }
            Course course = new Course();
            course.setName(form.getName());
            course.setDescription(form.getDescription());
            course=courseRepository.save(course);
            if (course==null){
                return new ResponseDto(false,"Bazaga saqalanmadi");
            }
            return new ResponseDto(true,"ok");
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseDto(false,e.getMessage());
        }
    }

    @Override
    public ResponseDto edit(Long id, CreateCourseForm form) {
        try {
             Optional<Course> cOp =courseRepository.findById(id);
             if (!cOp.isPresent()){
                 return new ResponseDto(false,"Course not found");
             }
            Course course = cOp.get();
            course.setName(form.getName());
            course.setDescription(form.getDescription());
            course=courseRepository.save(course);
            if (course==null){
                return new ResponseDto(false,"Bazaga saqalanmadi");
            }
            return new ResponseDto(true,"ok");
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseDto(false,e.getMessage());
        }
    }

    @Override
    public ResponseDto delete(Long id) {
        try {

            Optional<Course> course = courseRepository.findById(id);
            if (course.isEmpty()) {
                return new ResponseDto<>(false, "course topilmadi!!!");
            }

            Optional<Long> count = courseRepository.countGroupsByCourseId(id);

                if (count.get() > 0) {
                    return new ResponseDto<>(false, "Bu courseda grouhlar mavjud!!!");
                }

            courseRepository.deleteById(id);
            return new ResponseDto<>(true, "o'chirildi");
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseDto(false,e.getMessage());
        }
    }

    @Override
    public ResponseDto findAll(int page, int size) {
        Pageable pageable= PageRequest.of(page,size, Sort.by("id").descending());
        return new ResponseDto(true,"ok",courseRepository.findAll(pageable));
    }

    @Override
    public ResponseDto findCoursesForSelect() {
        return new ResponseDto<>(true, "ok",courseRepository.findCoursesForSelect());
    }

    @Override
    public ResponseDto search(String searching, int page, int size) throws Exception{
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Course> pages = courseRepository.search(searching,pageable);
        List<Course> list = pages.getContent();
        PageDataResponseDto<List<Course>> dto = new PageDataResponseDto(list,pages.getTotalElements());
        return new ResponseDto<>(true,"ok",dto);
    }


}
