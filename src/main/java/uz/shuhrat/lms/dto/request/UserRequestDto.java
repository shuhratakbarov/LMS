package uz.shuhrat.lms.dto.form.user;

import uz.shuhrat.lms.annotation.ValidPassword;

import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.List;

public record CreateUserForm (
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String email,
    @NotBlank String phone,
    @NotBlank String address,
    @NotBlank Date birthDate,
    @NotBlank String username,
    @ValidPassword String password,
    @NotBlank List<Long> groups,
    @NotBlank String role
){}
