package uz.shuhrat.lms.dto.request;

public record LessonScheduleRequestDto(
        Long groupId,
        Integer day,
        Integer startTime,
        Integer endTime,
        Long roomId
) {
}
