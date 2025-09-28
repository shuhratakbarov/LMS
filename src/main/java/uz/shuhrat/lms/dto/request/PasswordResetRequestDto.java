package uz.shuhrat.lms.dto.request;

import javax.validation.constraints.NotBlank;

public record PasswordResetRequestDto(@NotBlank String email) {
}

