package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.MarkAttendanceRequestDto;

public interface AttendanceService {
    GeneralResponseDto<?> markAttendance(MarkAttendanceRequestDto markAttendanceRequestDto) throws Exception;

    GeneralResponseDto<?> getAttendanceByLessonInstance(Long lessonInstanceId);
}
