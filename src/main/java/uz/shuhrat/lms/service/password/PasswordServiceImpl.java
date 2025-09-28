package uz.shuhrat.lms.service.password;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.PasswordResetToken;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.PasswordResetTokenRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.ChangePasswordRequestDto;
import uz.shuhrat.lms.dto.request.PasswordResetConfirmDto;
import uz.shuhrat.lms.dto.request.PasswordResetRequestDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.auth.AuthService;
import uz.shuhrat.lms.service.email.EmailService;

import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService{
    @Value("${password.reset.token.expiration}")
    private int passwordResetTokenExpiration;
    @Value("${spring.mail.username}")
    private String mailUsername;
    private final EmailService emailService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public GeneralResponseDto<?> createPasswordResetToken(PasswordResetRequestDto passwordResetRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(passwordResetRequestDto.email());
        if (userOptional.isEmpty()) {
            return new GeneralResponseDto<>(false, "Email not registered");
        }
        User user = userOptional.get();
        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .user(user)
                .token(rawToken)
                .expiryDate(new Date(System.currentTimeMillis() + passwordResetTokenExpiration))
                .build();
        emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
        if (passwordResetTokenRepository.save(passwordResetToken).getUser() != null) {
            return new GeneralResponseDto<>(true, "Please check your email from " + mailUsername + " and click the reset button");
        }
        return new GeneralResponseDto<>(false, "Try again later");
    }

    @Transactional
    public GeneralResponseDto<?> completePasswordReset(PasswordResetConfirmDto passwordResetConfirmDto) throws Exception {
        if (!passwordResetConfirmDto.passwordsMatch()) {
            return new GeneralResponseDto<>(false, "Passwords don't match");
        }
        User user = userRepository.findByEmail(passwordResetConfirmDto.email())
                .orElseThrow(() -> new Exception("Email not registered"));

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(passwordResetConfirmDto.token());

        if (tokenOpt.isEmpty()) {
            return new GeneralResponseDto<>(false, "Password Reset Token is not present");
        }

        PasswordResetToken token = tokenOpt.get();

        if (!token.getUser().getEmail().equals(passwordResetConfirmDto.email())) {
            return new GeneralResponseDto<>(false, "Invalid token for this user");
        }

        if (token.getExpiryDate().before(new Date(System.currentTimeMillis()))) {
            return new GeneralResponseDto<>(false, "Password Reset Token expired");
        }


        user.setPassword(passwordEncoder.encode(passwordResetConfirmDto.newPassword()));
        userRepository.save(user);
        passwordResetTokenRepository.delete(token);
        emailService.sendPasswordChangedNotification(user.getEmail());
        return new GeneralResponseDto<>(true, "Your password reset successfully");
    }

    @Override
    @Transactional
    public GeneralResponseDto<?> changePassword(ChangePasswordRequestDto changePasswordRequestDto, String authHeader) throws Exception {
        if (!changePasswordRequestDto.passwordsMatch()) {
            return new GeneralResponseDto<>(false, "Passwords don't match");
        }

        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null || !currentUser.isActive()) {
            return new GeneralResponseDto<>(false, "You are not allowed");
        }

        if (!passwordEncoder.matches(changePasswordRequestDto.oldPassword(), currentUser.getPassword())) {
            throw new Exception("Old (current) password is incorrect");
        }

        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        GeneralResponseDto<?> generalResponseDto = authService.logout(token);
        if (!generalResponseDto.isSuccess()) {
            return new GeneralResponseDto<>(false, generalResponseDto.getMessage());
        }

        currentUser.setPassword(passwordEncoder.encode(changePasswordRequestDto.newPassword()));

        if (!passwordEncoder.matches(changePasswordRequestDto.newPassword(), userRepository.save(currentUser).getPassword())) {
            return new GeneralResponseDto<>(false, "Password hasn't updated. Please try again later");
        }
        emailService.sendPasswordChangedNotification(currentUser.getEmail());
        return new GeneralResponseDto<>(true, "Password updated successfully. For security reasons, you will be logged out. Please login with your new password.");
    }
}
