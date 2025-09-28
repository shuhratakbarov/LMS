package uz.shuhrat.lms.controller.rest.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.request.MarkAttendanceRequestDto;
import uz.shuhrat.lms.service.admin.AttendanceService;

@RestController
@RequestMapping("/teacher/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<?> markAttendance(@RequestBody MarkAttendanceRequestDto markAttendanceRequestDto) throws Exception {
        return ResponseEntity.ok(attendanceService.markAttendance(markAttendanceRequestDto));
    }

    @GetMapping("/lesson/{lessonInstanceId}")
    public ResponseEntity<?> getAttendanceByLessonInstance(@PathVariable Long lessonInstanceId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByLessonInstance(lessonInstanceId));
    }
}