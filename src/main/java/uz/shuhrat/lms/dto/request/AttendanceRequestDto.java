package uz.shuhrat.lms.dto.request;

import java.util.UUID;

public record AttendanceRequestDto(
        UUID studentId,
        Long lessonInstanceId,
        boolean isPresent,
        int minutesLate
) {
}