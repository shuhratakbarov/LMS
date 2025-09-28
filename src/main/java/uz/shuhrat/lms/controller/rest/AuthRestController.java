package uz.shuhrat.lms.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.request.RefreshRequestDto;
import uz.shuhrat.lms.dto.request.LoginRequestDto;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestDto form) {
        return ResponseEntity.ok(authService.login(form));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        return ResponseEntity.ok(authService.logout(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequestDto refreshRequestDTO) {
        return ResponseEntity.ok(authService.refresh(refreshRequestDTO.refreshToken()));
    }
}
