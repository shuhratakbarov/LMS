package uz.shuhrat.lms.dto.request;

import lombok.Builder;
import lombok.Data;
import uz.shuhrat.lms.annotation.ValidPassword;

@Data
@Builder
public class ResetPasswordRequestDto {

    private String oldPassword;

    private  String token;

    @ValidPassword
    private String newPassword;
}
