package uz.shuhrat.lms.dto.request;

import uz.shuhrat.lms.annotation.ValidPassword;

import javax.validation.constraints.NotBlank;
import java.sql.Date;

public record UserRequestDto(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String email,
    @NotBlank String phone,
    @NotBlank String address,
    @NotBlank Date birthDate,
    @NotBlank String username,
    @ValidPassword String password,
    @NotBlank String role
){}
