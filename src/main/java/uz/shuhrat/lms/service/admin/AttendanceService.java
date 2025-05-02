package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.AttendanceDTO;
import uz.shuhrat.lms.dto.MarkAttendanceDTO;
import uz.shuhrat.lms.dto.ResponseDto;

import java.util.List;

public interface AttendanceService {
    ResponseDto<?> markAttendance(MarkAttendanceDTO markAttendanceDTO) throws Exception;

    ResponseDto<?> getAttendanceByLessonInstance(Long lessonInstanceId);
}
