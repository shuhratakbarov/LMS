package uz.shuhrat.lms.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.component.jwt.JwtService;
import uz.shuhrat.lms.db.domain.TokenBlacklist;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.admin.TokenBlacklistRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.response.LoginResponseDto;
import uz.shuhrat.lms.dto.response.UserResponseDto;
import uz.shuhrat.lms.dto.request.LoginRequestDto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;
    private final JwtService jwtService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public GeneralResponseDto<?> login(LoginRequestDto form) {
        try {
            User user = userRepository.findByUsername(form.username())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!passwordEncoder.matches(form.password(), user.getPassword())) {
                return GeneralResponseDto.error("Invalid credentials");
            }

            String accessToken = jwtService.generateAccessToken(form.username());
            String refreshToken = jwtService.generateRefreshToken(form.username());

            return GeneralResponseDto.success(
                    "Login successful",
                    LoginResponseDto.builder()
                            .user(new UserResponseDto(user.getFirstName(), user.getLastName(),
                                    user.getRole().toString(), user.getUsername(), 0))
                            .access_token(accessToken)
                            .refresh_token(refreshToken)
                            .accessExpiration(System.currentTimeMillis() + accessExpiration)
                            .build());
        } catch (Exception e) {
            return GeneralResponseDto.error("Login failed: " + e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> logout(String token) {
        try {
            if (token == null || jwtService.isTokenExpired(token)) {
                return GeneralResponseDto.error("Invalid or expired token");
            }
            if (tokenBlacklistRepository.findByToken(token).isPresent()) {
                return GeneralResponseDto.success("Already logged out", null); // Success instead of error
            }
            Instant expiration = jwtService.extractExpiration(token);
            tokenBlacklistRepository.save(new TokenBlacklist(token, expiration));
            return GeneralResponseDto.success("Logout successful", null);
        } catch (Exception e) {
            return GeneralResponseDto.error("Logout failed: " + e.getMessage());
        }
    }

    @Override
    public GeneralResponseDto<?> refresh(String refreshToken) {
        try {
            if (refreshToken == null || jwtService.isTokenExpired(refreshToken)) {
                return GeneralResponseDto.error("Invalid or expired refresh token");
            }
            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new Exception("User not found"));
            String newAccessToken = jwtService.generateAccessToken(username);
            return GeneralResponseDto.success(
                    "Token refreshed",
                    LoginResponseDto.builder()
                            .user(new UserResponseDto(user.getFirstName(), user.getLastName(), user.getRole().toString(), user.getUsername(), 0))
                            .access_token(newAccessToken)
                            .refresh_token(refreshToken)
                            .accessExpiration(System.currentTimeMillis() + accessExpiration)
                            .build());
        } catch (Exception e) {
            return GeneralResponseDto.error("Refresh failed: " + e.getMessage());
        }
    }
}
