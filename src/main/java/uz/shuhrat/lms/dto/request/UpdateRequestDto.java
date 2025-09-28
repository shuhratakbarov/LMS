package uz.shuhrat.lms.dto.request;

import uz.shuhrat.lms.enums.UpdateType;

public record UpdateRequestDto(
        String title,
        String body,
        UpdateType type,
        String role
) {}

