package uz.shuhrat.lms.controller.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.request.CourseRequestDto;
import uz.shuhrat.lms.service.admin.CourseService;

@RestController
@RequestMapping("/admin/course")
public class CourseRestController {
    private final CourseService courseService;

    @Autowired
    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<?> getCourseList(@RequestParam(required = false, name = "keyword") String keyword,
                                           @RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "20") int size) throws Exception {
        return ResponseEntity.ok(courseService.getCourseList(keyword, page, size));
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseRequestDto form) {
        return ResponseEntity.ok(courseService.save(form));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editCourse(@PathVariable Long id,
                                        @RequestBody CourseRequestDto form) {
        return ResponseEntity.ok(courseService.edit(id, form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.delete(id));
    }
}
