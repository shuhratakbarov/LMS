package uz.shuhrat.lms.dto.response;

import uz.shuhrat.lms.enums.ConversationOriginType;

import java.util.UUID;

public record ConversationSearchResultDto(
        UUID id,
        String name,
        String username,
        ConversationOriginType originType,
        boolean isGroup,
        String role
) {}