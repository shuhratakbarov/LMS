package uz.shuhrat.lms.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConversationSummaryDto(
        UUID id,
        boolean isGroup,
        String name,
        String lastMessageSnippet,
        Instant lastMessageAt,
        int unreadCount
) {}

