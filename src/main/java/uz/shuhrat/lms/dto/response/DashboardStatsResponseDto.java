package uz.shuhrat.lms.dto.response;

public record DashboardStatsResponseDto(
        String title,
        String suffix,
        int value
) {
}