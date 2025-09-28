package uz.shuhrat.lms.service.password;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.ChangePasswordRequestDto;
import uz.shuhrat.lms.dto.request.PasswordResetConfirmDto;
import uz.shuhrat.lms.dto.request.PasswordResetRequestDto;

public interface PasswordService {
//    void changePassword(Long userId, String currentPassword, String newPassword);
    GeneralResponseDto<?> createPasswordResetToken(PasswordResetRequestDto passwordResetRequestDto) throws Exception;
    GeneralResponseDto<?> completePasswordReset(PasswordResetConfirmDto passwordResetConfirmDto) throws Exception;

    GeneralResponseDto<?> changePassword(ChangePasswordRequestDto changePasswordRequestDto, String authHeader) throws Exception;
}
