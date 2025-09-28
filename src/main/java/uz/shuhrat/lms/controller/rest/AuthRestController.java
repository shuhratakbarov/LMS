package uz.shuhrat.lms.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.RefreshRequestDTO;
import uz.shuhrat.lms.dto.form.LoginForm;
import uz.shuhrat.lms.dto.request.ChangePasswordRequestDto;
import uz.shuhrat.lms.service.admin.UserService;
import uz.shuhrat.lms.service.auth.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthRestController {
    private final AuthService authService;

    @Autowired
    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form) throws Exception {
        return ResponseEntity.ok(authService.login(form));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        return ResponseEntity.ok(authService.logout(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequestDTO refreshRequestDTO) {
        return ResponseEntity.ok(authService.refresh(refreshRequestDTO.getRefreshToken()));
    }
}
