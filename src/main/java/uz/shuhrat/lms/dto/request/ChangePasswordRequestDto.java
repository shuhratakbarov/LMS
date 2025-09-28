package uz.shuhrat.lms.dto.request.password;

import uz.shuhrat.lms.annotation.ValidPassword;

import javax.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(
        @NotBlank String oldPassword,
        @NotBlank @ValidPassword String newPassword,
        @NotBlank String confirmPassword
) {
    public boolean passwordsMatch() {
        return newPassword.equals(confirmPassword);
    }
}



