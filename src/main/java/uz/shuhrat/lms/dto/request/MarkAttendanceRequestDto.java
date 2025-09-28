package uz.shuhrat.lms.dto.request;

import uz.shuhrat.lms.dto.request.AttendanceRequestDto;

import java.util.List;

public record MarkAttendanceRequestDto(
        Long lessonInstanceId,
        List<AttendanceRequestDto> attendanceRequestDtoList
) {
}