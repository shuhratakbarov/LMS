package uz.shuhrat.lms.dto.response;

import java.sql.Date;
import java.time.Instant;

public record UserProfileDetailsResponseDto(
        String firstName,
        String lastName,
        String roleName,
        String username,
        String email,
        String phone,
        String address,
        Date birthDate,
        Instant lastSeen,
        int courseCount,
        int groupCount
) {
}
