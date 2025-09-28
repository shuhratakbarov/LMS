package uz.shuhrat.lms.rest.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.db.domain.Attendance;
import uz.shuhrat.lms.dto.AttendanceDTO;
import uz.shuhrat.lms.dto.MarkAttendanceDTO;
import uz.shuhrat.lms.service.admin.AttendanceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<?> markAttendance(@RequestBody MarkAttendanceDTO markAttendanceDTO) throws Exception {
        return ResponseEntity.ok(attendanceService.markAttendance(markAttendanceDTO));
    }

    @GetMapping("/lesson/{lessonInstanceId}")
    public ResponseEntity<?> getAttendanceByLessonInstance(@PathVariable Long lessonInstanceId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByLessonInstance(lessonInstanceId));
    }
}