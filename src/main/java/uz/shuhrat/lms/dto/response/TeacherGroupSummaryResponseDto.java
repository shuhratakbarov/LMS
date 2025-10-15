package uz.shuhrat.lms.dto.response;

public record TeacherGroupSummaryResponseDto(
        Long id,
        String groupName,
        Long courseId,
        String courseName,
        Long studentCount,
        Long taskCount
) {
}
