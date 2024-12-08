package uz.shuhrat.lms.dto.form;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginForm {
    private String username;
    private String password;
}
