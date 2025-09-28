package uz.shuhrat.lms.controller.rest.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.request.TaskRequestDto;
import uz.shuhrat.lms.dto.request.EvaluateHomeworkRequestDto;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.service.admin.LessonScheduleService;
import uz.shuhrat.lms.service.admin.UpdateService;
import uz.shuhrat.lms.service.teacher.TaskService;
import uz.shuhrat.lms.service.teacher.TeacherService;

import java.util.UUID;

@RestController
@RequestMapping("/teacher")
public class TeacherRestController {
    private final UpdateService updateService;
    private final TaskService taskService;
    private final TeacherService teacherService;
    private final LessonScheduleService lessonScheduleService;

    @Autowired
    public TeacherRestController(UpdateService updateService, TaskService taskService, TeacherService teacherService, LessonScheduleService lessonScheduleService) {
        this.updateService = updateService;
        this.taskService = taskService;
        this.teacherService = teacherService;
        this.lessonScheduleService = lessonScheduleService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getStudentUpdates() {
        return ResponseEntity.ok(updateService.getByRole(Role.TEACHER));
    }

    @GetMapping("/group")
    public ResponseEntity<?> getGroupsOfTeacher(@RequestParam(name = "keyword") String keyword,
                                                @RequestParam(required = false, defaultValue = "0") int page,
                                                @RequestParam(required = false, defaultValue = "5") int size) throws Exception {
        return ResponseEntity.ok(teacherService.getGroups(keyword, page, size));
    }

    @GetMapping("/task")
    public ResponseEntity<?> getTasksOfGroup(@RequestParam("group-id") Long groupId) {
        return ResponseEntity.ok(taskService.findAllWithFiles(groupId));
    }

    @GetMapping("/lesson")
    public ResponseEntity<?> getLessonsOfTeacher() {
        return ResponseEntity.ok(lessonScheduleService.getTeacherSchedule());
    }

    @PostMapping("/task")
    public ResponseEntity<?> createTask(@RequestParam("file") MultipartFile file,
                                        @ModelAttribute TaskRequestDto form) throws Exception {
        return ResponseEntity.ok(taskService.saveTask(file, form));
    }

    @PutMapping("/task/{taskId}")
    public ResponseEntity<?> editTask(@PathVariable String taskId,
                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                      @ModelAttribute TaskRequestDto form) throws Exception {
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
                                              @RequestBody EvaluateHomeworkRequestDto homework) {
        return ResponseEntity.ok(teacherService.evaluateHomework(homeworkId, homework));
    }
}
