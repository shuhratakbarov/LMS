package uz.shuhrat.lms.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.request.ChangePasswordRequestDto;
import uz.shuhrat.lms.dto.request.PasswordResetConfirmDto;
import uz.shuhrat.lms.dto.request.PasswordResetRequestDto;
import uz.shuhrat.lms.service.password.PasswordService;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordRestController {

    private final PasswordService passwordService;

    @PostMapping("/reset/request")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequestDto passwordResetRequestDto) throws Exception {
        return ResponseEntity.ok(passwordService.createPasswordResetToken(passwordResetRequestDto));
    }

    @PostMapping("/reset/confirm")
    public ResponseEntity<?> confirmReset(@RequestBody PasswordResetConfirmDto request) throws Exception {;
        return ResponseEntity.ok(passwordService.completePasswordReset(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody ChangePasswordRequestDto changePasswordRequestDto) throws Exception {
        return ResponseEntity.ok(passwordService.changePassword(changePasswordRequestDto, authHeader));
    }

//    @PostMapping("/change")
//    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
//        passwordService.changePassword(
//                request.getUserId(),
//                request.getCurrentPassword(),
//                request.getNewPassword()
//        );
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/user/changePassword")
//    public String showChangePasswordPage(Locale locale, Model model,
//                                         @RequestParam("token") String token) {
//        String result = securityService.validatePasswordResetToken(token);
//        if(result != null) {
//            String message = messages.getMessage("auth.message." + result, null, locale);
//            return "redirect:/login.html?lang="
//                   + locale.getLanguage() + "&message=" + message;
//        } else {
//            model.addAttribute("token", token);
//            return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
//        }
//    }
//
//    @PostMapping("/user/savePassword")
//    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
//
//        String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());
//
//        if(result != null) {
//            return new GenericResponse(messages.getMessage(
//                    "auth.message." + result, null, locale));
//        }
//
//        Optional user = userService.getUserByPasswordResetToken(passwordDto.getToken());
//        if(user.isPresent()) {
//            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
//            return new GenericResponse(messages.getMessage(
//                    "message.resetPasswordSuc", null, locale));
//        } else {
//            return new GenericResponse(messages.getMessage(
//                    "auth.message.invalid", null, locale));
//        }
//    }

    // add password expiration policy if needed
}
