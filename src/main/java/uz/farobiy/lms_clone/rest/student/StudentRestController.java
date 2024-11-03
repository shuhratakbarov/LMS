package uz.farobiy.lms_clone.rest.student;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lms_clone.service.student.HomeworkService;

import uz.farobiy.lms_clone.service.student.StudentService;

import java.util.UUID;

@RestController
@RequestMapping("/student")
public class StudentRestController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private HomeworkService homeworkService;


    @GetMapping("/my-groups")
    public ResponseEntity getGroupsOfStudent() {
        return ResponseEntity.ok(studentService.getGroupsOfStudent());}

    @GetMapping("/homework/{groupId}")
    public ResponseEntity getHomeworks(@PathVariable Long groupId) {
        return ResponseEntity.ok(studentService.getHomeworksOfStudent(groupId));
    }

    @PostMapping("/upload-homework/{taskId}")
    public ResponseEntity saveHomework(@PathVariable UUID taskId,
                                       @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(homeworkService.saveHomework(taskId, null, file));
    }

    @PutMapping("/re-upload-homework/{taskId}")
    public ResponseEntity updateHomework(@PathVariable UUID taskId,
                                         @RequestParam(value = "homeworkId", required = false) UUID homeworkId,
                                         @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(homeworkService.saveHomework(taskId, homeworkId, file));
    }

    @GetMapping("/get-count")
    public ResponseEntity getCount() {
        return ResponseEntity.ok(studentService.getHomeworkCount());
    }
}

