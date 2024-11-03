package uz.farobiy.lms_clone.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.farobiy.lms_clone.db.domain.User;

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
