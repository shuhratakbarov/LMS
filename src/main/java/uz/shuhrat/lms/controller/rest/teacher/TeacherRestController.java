package uz.shuhrat.lms.controller.rest.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.CreateExamRequestDto;
import uz.shuhrat.lms.dto.request.TaskRequestDto;
import uz.shuhrat.lms.dto.request.EvaluateHomeworkRequestDto;
import uz.shuhrat.lms.dto.request.UpdateExamRequestDto;
import uz.shuhrat.lms.dto.response.ExamDetailsDto;
import uz.shuhrat.lms.dto.response.ExamResponseDto;
import uz.shuhrat.lms.dto.response.ExamStatisticsDto;
import uz.shuhrat.lms.dto.response.StudentSubmissionDto;
import uz.shuhrat.lms.enums.ExamStatus;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.service.ExamService;
import uz.shuhrat.lms.service.admin.LessonScheduleService;
import uz.shuhrat.lms.service.admin.UpdateService;
import uz.shuhrat.lms.service.teacher.QuizGeneratorService;
import uz.shuhrat.lms.service.teacher.TaskService;
import uz.shuhrat.lms.service.teacher.TeacherService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherRestController {
    private final UpdateService updateService;
    private final TaskService taskService;
    private final TeacherService teacherService;
    private final LessonScheduleService lessonScheduleService;
    private final ExamService examService;
    private final QuizGeneratorService quizGeneratorService;

    @Autowired
    public TeacherRestController(UpdateService updateService, TaskService taskService, TeacherService teacherService, LessonScheduleService lessonScheduleService, ExamService examService, QuizGeneratorService quizGeneratorService) {
        this.updateService = updateService;
        this.taskService = taskService;
        this.teacherService = teacherService;
        this.lessonScheduleService = lessonScheduleService;
        this.examService = examService;
        this.quizGeneratorService = quizGeneratorService;
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

    @GetMapping("/course-id")
    public ResponseEntity<GeneralResponseDto<?>> getTeacherCourseIds() {
        return ResponseEntity.ok(teacherService.findCoursesForSelect());
    }

    @GetMapping("/exam")
    public ResponseEntity<List<ExamResponseDto>> getMyExams() {
        List<ExamResponseDto> exams = examService.getExamsByTeacher();
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponseDto> getExamById(@PathVariable Long examId) {
        ExamResponseDto exam = examService.getExamById(examId);
        return ResponseEntity.ok(exam);
    }

    @GetMapping("/exam/{examId}/details")
    public ResponseEntity<ExamDetailsDto> getExamDetails(
            @PathVariable Long examId) {
        ExamDetailsDto exam = examService.getExamDetailsForTeacher(examId);
        return ResponseEntity.ok(exam);
    }

    @GetMapping("/exam/{examId}/statistics")
    public ResponseEntity<ExamStatisticsDto> getExamStatistics(
            @PathVariable Long examId) {
        ExamStatisticsDto statistics = examService.getExamStatistics(examId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/exam/{examId}/submissions")
    public ResponseEntity<List<StudentSubmissionDto>> getExamSubmissions(
            @PathVariable Long examId) {
        List<StudentSubmissionDto> submissions = examService.getExamSubmissions(examId);
        return ResponseEntity.ok(submissions);
    }

    @PostMapping("/exam")
    public ResponseEntity<ExamResponseDto> createExam(
            @Valid @RequestBody CreateExamRequestDto request) {
        ExamResponseDto response = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/exam/generate-questions")
    public ResponseEntity<String> generateQuiz(
            @RequestParam String topic,
            @RequestParam String content,
            @RequestParam int amount,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String areas) {
        String result = quizGeneratorService.generateQuiz(topic, content, amount, difficulty, areas);
        String cleanResult = result
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        return ResponseEntity.ok(cleanResult);
    }

    @PutMapping("/exam/{examId}")
    public ResponseEntity<ExamResponseDto> updateExam(
            @PathVariable Long examId,
            @Valid @RequestBody UpdateExamRequestDto request) {
        ExamResponseDto response = examService.updateExam(examId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/exam/{examId}/status")
    public ResponseEntity<ExamResponseDto> updateExamStatus(
            @PathVariable Long examId,
            @RequestParam ExamStatus status) {
        ExamResponseDto response = examService.updateExamStatus(examId, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/exam/{examId}")
    public ResponseEntity<Void> deleteExam(
            @PathVariable Long examId) {
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }
}
