package uz.shuhrat.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String roleName;
    private String username;
    private int notificationCount;
}
