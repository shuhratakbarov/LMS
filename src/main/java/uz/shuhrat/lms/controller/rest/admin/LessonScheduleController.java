package uz.shuhrat.lms.controller.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.request.LessonScheduleRequestDto;
import uz.shuhrat.lms.service.admin.LessonScheduleService;

@RestController
@RequestMapping("/admin/lesson")
public class LessonScheduleController {

    private final LessonScheduleService lessonScheduleService;

    @Autowired
    public LessonScheduleController(LessonScheduleService lessonScheduleService) {
        this.lessonScheduleService = lessonScheduleService;
    }

    @GetMapping
    public ResponseEntity<?> getLessonSchedule(@RequestParam(required = false, name = "page") int page,
                                               @RequestParam(required = false, name = "size") int size,
                                               @RequestParam(required = false, name = "keyword") String keyword) {
        return ResponseEntity.ok(lessonScheduleService.getAdminSchedule(keyword, page, size));
    }

    @PostMapping("/check-conflict")
    public ResponseEntity<?> checkConflict(@RequestBody LessonScheduleRequestDto lessonScheduleResponseDTO,
                                           @RequestParam(required = false, name = "lesson-schedule-id") Long lessonScheduleId) throws Exception {
        return ResponseEntity.ok(lessonScheduleService.checkForScheduleConflicts(lessonScheduleResponseDTO, lessonScheduleId));
    }

    @PostMapping
    public ResponseEntity<?> createLessonSchedule(@RequestBody LessonScheduleRequestDto lessonScheduleResponseDTO) throws Exception {
        return ResponseEntity.ok(lessonScheduleService.createLessonSchedule(lessonScheduleResponseDTO));
    }

    @PutMapping("/{lessonScheduleId}")
    public ResponseEntity<?> updateLessonSchedule(@PathVariable Long lessonScheduleId,
                                                  @RequestBody LessonScheduleRequestDto lessonScheduleResponseDTO) throws Exception {
        return ResponseEntity.ok(lessonScheduleService.updateLessonSchedule(lessonScheduleId, lessonScheduleResponseDTO));
    }

    @DeleteMapping("/{lessonScheduleId}")
    public ResponseEntity<?> deleteLessonSchedule(@PathVariable Long lessonScheduleId) {
        return ResponseEntity.ok(lessonScheduleService.deleteLessonSchedule(lessonScheduleId));
    }
}