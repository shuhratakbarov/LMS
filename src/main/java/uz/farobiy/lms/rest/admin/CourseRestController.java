package uz.farobiy.lms.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.farobiy.lms.dto.form.CreateCourseForm;
import uz.farobiy.lms.service.admin.CourseService;

@RestController
@RequestMapping("/admin/course")
public class CourseRestController {
    private final CourseService courseService;

    @Autowired
    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@RequestBody CreateCourseForm form) {
        return ResponseEntity.ok(courseService.save(form));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editCourse(@PathVariable Long id,
                                        @RequestBody CreateCourseForm form) {
        return ResponseEntity.ok(courseService.edit(id, form));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.delete(id));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCourseList(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "20") int size) {
        return ResponseEntity.ok(courseService.findAll(page, size));
    }

    @GetMapping("/course-id-and-name")
    public ResponseEntity<?> findCoursesForSelect() {
        return ResponseEntity.ok(courseService.findCoursesForSelect());
    }

    @GetMapping("/search")
    public ResponseEntity<?> getSearchCourseList(@RequestParam(name = "searching") String keyword,
                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                 @RequestParam(required = false, defaultValue = "20") int size) throws Exception {
        return ResponseEntity.ok(courseService.search(keyword, page, size));
    }
}
