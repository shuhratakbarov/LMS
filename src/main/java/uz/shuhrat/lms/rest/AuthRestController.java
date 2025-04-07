package uz.shuhrat.lms.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.RefreshRequestDTO;
import uz.shuhrat.lms.dto.form.LoginForm;
import uz.shuhrat.lms.service.admin.UserService;

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

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        return ResponseEntity.ok(userService.logout(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequestDTO refreshRequestDTO) {
        return ResponseEntity.ok(userService.refresh(refreshRequestDTO.getRefreshToken()));
    }
}
