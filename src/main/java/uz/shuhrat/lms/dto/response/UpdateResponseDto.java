package uz.shuhrat.lms.dto.response;

import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.enums.UpdateType;

import java.util.Date;
import java.util.List;

public record UpdateResponseDto(
        Long id,
        String title,
        String body,
        UpdateType type,
        List<Role> roles,
        Date createdAt,
        Date updatedAt
) {}
