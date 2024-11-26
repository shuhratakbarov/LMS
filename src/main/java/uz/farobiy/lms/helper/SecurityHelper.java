package uz.farobiy.lms.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.farobiy.lms.db.domain.User;

public class SecurityHelper {
    public static User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return (User) authentication.getPrincipal();
        } catch (Exception e) {
            System.out.println("Security Helper: " + e.getMessage());
            return null;
        }
    }
}
