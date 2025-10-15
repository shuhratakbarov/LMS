package uz.shuhrat.lms.dto.request;

import javax.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank String username,
        @NotBlank String password
) {
}