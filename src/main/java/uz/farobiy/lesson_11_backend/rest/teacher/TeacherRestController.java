package uz.farobiy.lesson_11_backend.rest.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lesson_11_backend.dto.ResponseDto;
import uz.farobiy.lesson_11_backend.dto.form.CreateTaskForm;
import uz.farobiy.lesson_11_backend.dto.form.EvaluateHomework;
import uz.farobiy.lesson_11_backend.service.file.AttachmentService;
import uz.farobiy.lesson_11_backend.service.file.FileService;
import uz.farobiy.lesson_11_backend.service.student.HomeworkService;
import uz.farobiy.lesson_11_backend.service.teacher.TaskService;
import uz.farobiy.lesson_11_backend.service.admin.UserService;
import uz.farobiy.lesson_11_backend.service.teacher.TeacherService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;


@RestController
@RequestMapping("/teacher")
public class TeacherRestController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    AttachmentService attachmentService;

//    @GetMapping("/students-of-group/{groupId}")
//    public ResponseEntity studentsOfGroup(@PathVariable Long groupId,
//                                          @RequestParam(required = false,defaultValue = "0") int page,
//                                          @RequestParam(required = false,defaultValue = "6") int size) throws Exception{
//        return ResponseEntity.ok(userService.getStudentsOfGroup(groupId,page,size));
//    }



//
//    @PostMapping("/upload")
//    public ResponseEntity<ResponseDto> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
//        return attachmentService.uploadFile(file);
//    }
//
//    @GetMapping("/download/{fileId}")
//    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("fileId") UUID fileId) throws Exception {
//        return attachmentService.downloadFile(fileId);
//    }
//
//    @DeleteMapping("/delete/{fileId}")
//    public ResponseEntity<ResponseDto> deleteFile(@PathVariable UUID fileId) throws Exception {
//        return attachmentService.deleteFile(fileId);
//    }
//
//    @PostMapping("/create-task")
//    public ResponseEntity<ResponseDto> createTask(@RequestParam("file") MultipartFile file, @RequestBody CreateTaskForm form) throws Exception {
//        return ResponseEntity.ok(taskService.saveTask(file,form));
//    }
//
//}

    @GetMapping("/my-groups")
    public ResponseEntity getGroupsOfTeacher(@RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "6") int size) throws Exception {
        return ResponseEntity.ok(teacherService.getGroups(page, size));
    }

    @GetMapping("/groups-of-teacher/{courseId}")
    public ResponseEntity getCoursesOfTeacher(@PathVariable("courseId") String courseId,
                                              @RequestParam(required = false, defaultValue = "0") int page,
                                              @RequestParam(required = false, defaultValue = "6") int size) throws Exception {
        return ResponseEntity.ok(teacherService.getGroupsByCourseId(courseId, page, size));
    }


    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(fileService.save(file));
    }

    @PostMapping("/create-task" /*,consumes = {"multipart/form-data"}*/)
    public ResponseEntity<ResponseDto> createTask(@RequestParam("file") MultipartFile file,
                                                  @ModelAttribute CreateTaskForm form) throws Exception {
        return ResponseEntity.ok(taskService.saveTask(file, form));
    }

    @PutMapping("/edit-task/{taskId}")
    public ResponseEntity editTask(@PathVariable UUID taskId, @RequestParam("file") MultipartFile file, @RequestBody CreateTaskForm form) throws Exception {
        return ResponseEntity.ok(taskService.editTask(taskId, form, file));
    }

    @GetMapping("/students-of-group/{groupId}")
    public ResponseEntity studentOfGroup(@PathVariable Long groupId,
                                         @RequestParam(required = false, name = "taskId") String taskId,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(teacherService.getStudentOfGroup(groupId, taskId, page, size));
    }

    @GetMapping("/list-of-tasks/{groupId}")
    public ResponseEntity getTasks(@PathVariable("groupId") Long groupId) throws Exception {
        return ResponseEntity.ok(taskService.findAllWithFiles(groupId));
    }

    @PostMapping("/evaluate/{homeworkId}")
    public ResponseEntity evaluateHomework(@PathVariable UUID homeworkId,
                                           @RequestBody EvaluateHomework homework) {
        return ResponseEntity.ok(teacherService.evaluateHomework(homeworkId, homework));
    }

}

