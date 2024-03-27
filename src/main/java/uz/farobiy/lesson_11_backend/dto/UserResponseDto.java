package uz.farobiy.lesson_11_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.farobiy.lesson_11_backend.db.domain.Role;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
   private String firstName;
   private String lastName;
   private String roleName;

}
