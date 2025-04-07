package uz.shuhrat.lms.rest.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.form.CreateTaskForm;
import uz.shuhrat.lms.dto.form.EvaluateHomework;
import uz.shuhrat.lms.service.teacher.TaskService;
import uz.shuhrat.lms.service.teacher.TeacherService;

import java.util.UUID;

@RestController
@RequestMapping("/teacher")
public class TeacherRestController {
    private final TaskService taskService;
    private final TeacherService teacherService;

    @Autowired
    public TeacherRestController(TaskService taskService, TeacherService teacherService) {
        this.taskService = taskService;
        this.teacherService = teacherService;
    }

    @GetMapping("/group")
    public ResponseEntity<?> getGroupsOfTeacher(@RequestParam(name = "keyword") String keyword,
                                                @RequestParam(required = false, defaultValue = "0") int page,
                                                @RequestParam(required = false, defaultValue = "5") int size) {
        return ResponseEntity.ok(teacherService.getGroups(keyword, page, size));
    }

    @GetMapping("/task")
    public ResponseEntity<?> getTasksOfGroup(@RequestParam("group-id") Long groupId) {
        return ResponseEntity.ok(taskService.findAllWithFiles(groupId));
    }

    @PostMapping("/task")
    public ResponseEntity<?> createTask(@RequestParam("file") MultipartFile file,
                                        @ModelAttribute CreateTaskForm form) throws Exception {
        return ResponseEntity.ok(taskService.saveTask(file, form));
    }

    @PutMapping("/task/{taskId}")
    public ResponseEntity<?> editTask(@PathVariable String taskId,
                                      @RequestParam("file") MultipartFile file,
                                      @ModelAttribute CreateTaskForm form) throws Exception {
        return ResponseEntity.ok(taskService.editTask(UUID.fromString(taskId), form, file));
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable String taskId) throws Exception {
        return ResponseEntity.ok(taskService.deleteTask(UUID.fromString(taskId)));
    }

    @GetMapping("/homework")
    public ResponseEntity<?> getHomeworkList(@RequestParam(name = "task-id") String taskId,
                                             @RequestParam(name = "group-id") String groupId,
                                             @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(teacherService.getHomeworkList(Long.parseLong(groupId), taskId, page, size));
    }

    @PatchMapping("/homework/{homeworkId}")
    public ResponseEntity<?> evaluateHomework(@PathVariable UUID homeworkId,
                                              @RequestBody EvaluateHomework homework) {
        return ResponseEntity.ok(teacherService.evaluateHomework(homeworkId, homework));
    }
}
