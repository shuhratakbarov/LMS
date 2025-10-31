package uz.shuhrat.lms.controller.rest.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.request.SubmitExamRequestDto;
import uz.shuhrat.lms.dto.response.*;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.service.ExamService;
import uz.shuhrat.lms.service.admin.LessonScheduleService;
import uz.shuhrat.lms.service.admin.UpdateService;
import uz.shuhrat.lms.service.student.HomeworkService;
import uz.shuhrat.lms.service.student.StudentService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentRestController {
    private final StudentService studentService;
    private final HomeworkService homeworkService;
    private final LessonScheduleService lessonScheduleService;
    private final UpdateService updateService;
    private final ExamService examService;

    @Autowired
    public StudentRestController(StudentService studentService, HomeworkService homeworkService, LessonScheduleService lessonScheduleService, UpdateService updateService, ExamService examService) {
        this.studentService = studentService;
        this.homeworkService = homeworkService;
        this.lessonScheduleService = lessonScheduleService;
        this.updateService = updateService;
        this.examService = examService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getStudentUpdates() {
        return ResponseEntity.ok(updateService.getByRole(Role.STUDENT));
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

    @GetMapping("/homework/notification")
    public ResponseEntity<?> getHomeworkNotification() {
        return ResponseEntity.ok(studentService.getHomeworkNotification());
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

    @GetMapping("/exam/{examId}")
    public ResponseEntity<ExamResponseDto> getExamById(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }

    @GetMapping("/exam/active")
    public ResponseEntity<List<ExamResponseDto>> getActiveExamsForCourse() {
        return ResponseEntity.ok(examService.getActiveExamsForCourse());
    }

    @GetMapping("/exam/{examId}/take")
    public ResponseEntity<ExamWithQuestionsDto> getExamForTaking(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamForStudent(examId));
    }

    @GetMapping("/exam/history")
    public ResponseEntity<List<StudentExamHistoryDto>> getStudentExamHistory() {
        return ResponseEntity.ok(examService.getStudentExamHistory());
    }

    @GetMapping("/exam/in-progress")
    public ResponseEntity<List<InProgressExamDto>> getInProgressExams() {
        return ResponseEntity.ok(examService.getInProgressExams());
    }

    @GetMapping("/exam/{attendanceId}/result")
    public ResponseEntity<ExamResultDto> getExamResult(@PathVariable Long attendanceId) {
        return ResponseEntity.ok(examService.getExamResult(attendanceId));
    }

    @PostMapping("/exam/{examId}/start")
    public ResponseEntity<AttendanceResponseDto> startExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.startExam(examId));
    }

    @GetMapping("/exam/{examId}/continue")
    public ResponseEntity<ExamWithQuestionsDto> continueExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.continueExam(examId));
    }

    @PostMapping("/exam/{attendanceId}/submit")
    public ResponseEntity<SubmitExamResponseDto> submitExam(@PathVariable Long attendanceId,
                                                            @Valid @RequestBody SubmitExamRequestDto request) {
        return ResponseEntity.ok(examService.submitExam(attendanceId, request));
    }
}

