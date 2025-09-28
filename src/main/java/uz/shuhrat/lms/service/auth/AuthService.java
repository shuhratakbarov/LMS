package uz.shuhrat.lms.service.auth;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.LoginRequestDto;

public interface AuthService {
    GeneralResponseDto<?> login(LoginRequestDto form);

    GeneralResponseDto<?> logout(String token);

    GeneralResponseDto<?> refresh(String refreshToken);

}
