package uz.shuhrat.lms.db.domain.enums;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

public enum Role implements GrantedAuthority {
    STUDENT,
    TEACHER,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

    public static boolean isRoleValid(String roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        return Arrays.stream(Role.values())
                .anyMatch(role -> role.name().equalsIgnoreCase(roleName));
    }
}