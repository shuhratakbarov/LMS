package uz.shuhrat.lms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginResponseDto {
    private String access_token;
    private String refresh_token;
    private Long expireDate;
}
