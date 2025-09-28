package uz.shuhrat.lms.dto.request.password;

import javax.validation.constraints.NotBlank;

public record PasswordResetRequestDto(@NotBlank String email) {
}

