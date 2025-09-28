package uz.shuhrat.lms.dto.response;

public record UserResponseDto(
        String firstName,
        String lastName,
        String roleName,
        String username,
        int notificationCount
) {
}