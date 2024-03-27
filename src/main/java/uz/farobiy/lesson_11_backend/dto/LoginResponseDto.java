package uz.farobiy.lesson_11_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class LoginResponseDto {
   private String access_token;
   private String refresh_token;
   private Long expireDate;
}
