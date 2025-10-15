package uz.shuhrat.lms.dto.response;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String access_token,
        String refresh_token,
        Long accessExpiration,
        UserResponseDto user
) {
}