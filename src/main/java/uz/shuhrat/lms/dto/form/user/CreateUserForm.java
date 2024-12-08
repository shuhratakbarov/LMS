package uz.shuhrat.lms.dto.form.user;

import lombok.*;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserForm {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Date birthDate;
    private String username;
    private String password;
    private List<Long> groups;
    private Long roleId;
}
