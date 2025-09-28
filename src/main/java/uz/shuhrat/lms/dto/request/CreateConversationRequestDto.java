package uz.shuhrat.lms.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateConversationRequestDto(
        boolean isGroup,
        String name,
        String username,
        List<UUID> participantIds
) {
}