package uz.farobiy.lms.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.farobiy.lms.dto.form.LoginForm;
import uz.farobiy.lms.service.admin.UserService;

@RestController
@RequestMapping("/auth")
public class AuthRestController {
    private final UserService userService;

    @Autowired
    public AuthRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form) throws Exception {
        return ResponseEntity.ok(userService.login(form));
    }
}
