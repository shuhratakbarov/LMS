package uz.farobiy.lesson_11_backend.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.farobiy.lesson_11_backend.config.SecurityConfig;
import uz.farobiy.lesson_11_backend.db.domain.User;

public class SecurityHelper {
    public static User getCurrentUser(){
        try {
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            return (User) authentication.getPrincipal();
        }catch (Exception e){
            System.out.println("Mana : " + e.getMessage());
            return null;
        }
    }
}
