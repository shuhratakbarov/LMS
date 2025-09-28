package uz.shuhrat.lms.rest.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.service.admin.LessonScheduleService;
import uz.shuhrat.lms.service.student.HomeworkService;

import uz.shuhrat.lms.service.student.StudentService;

import java.util.UUID;

@RestController
@RequestMapping("/student")
public class StudentRestController {
    private final StudentService studentService;
    private final HomeworkService homeworkService;
    private final LessonScheduleService lessonScheduleService;

    @Autowired
    public StudentRestController(StudentService studentService, HomeworkService homeworkService, LessonScheduleService lessonScheduleService) {
        this.studentService = studentService;
        this.homeworkService = homeworkService;
        this.lessonScheduleService = lessonScheduleService;
    }

    @GetMapping("/group")
    public ResponseEntity<?> getStudentGroupList(@RequestParam(name = "keyword", required = false) String keyword,
                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                 @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(studentService.getStudentGroupList(keyword, page, size));
    }

    @GetMapping("/lesson")
    public ResponseEntity<?> getLessonsOfStudent() {
        return ResponseEntity.ok(lessonScheduleService.getStudentSchedule());
    }

    @GetMapping("/get-count")
    public ResponseEntity<?> getCount() {
        return ResponseEntity.ok(studentService.getHomeworkCount());
    }

    @GetMapping("/homework")
    public ResponseEntity<?> getHomeworks(@RequestParam(name = "group-id") Long groupId,
                                          @RequestParam(required = false, defaultValue = "0") int page,
                                          @RequestParam(required = false, defaultValue = "5") int size) throws Exception {
        return ResponseEntity.ok(studentService.getHomeworksOfStudent(groupId, page, size));
    }

    @PostMapping("/homework")
    public ResponseEntity<?> saveHomework(@RequestParam(name = "task-id") UUID taskId,
                                          @RequestParam(name = "file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(homeworkService.saveHomework(taskId, null, file));
    }

    @PatchMapping("/homework/{id}")
    public ResponseEntity<?> updateHomework(@PathVariable(value = "id") UUID homeworkId,
                                            @RequestParam(name = "task-id") UUID taskId,
                                            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(homeworkService.saveHomework(taskId, homeworkId, file));
    }
}

