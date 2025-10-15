package uz.shuhrat.lms.dto.response;

public record LessonScheduleResponseDto(
        Long id,
        Long groupId,
        String groupName,
        Integer day,
        Integer startTime,
        Integer endTime,
        Long roomId,
        String roomName
) {
}