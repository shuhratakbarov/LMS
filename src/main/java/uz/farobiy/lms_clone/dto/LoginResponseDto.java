package uz.farobiy.lms_clone.dto;

import lombok.Builder;
import lombok.Data;
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
