package uz.shuhrat.lms.dto.request.password;

import uz.shuhrat.lms.annotation.ValidPassword;

import javax.validation.constraints.NotBlank;

public record PasswordResetConfirmDto(
        @NotBlank String email,
        @NotBlank String token,
        @ValidPassword String newPassword,
        @NotBlank String confirmPassword
) {
    public boolean passwordsMatch() {
        return newPassword.equals(confirmPassword);
    }
}