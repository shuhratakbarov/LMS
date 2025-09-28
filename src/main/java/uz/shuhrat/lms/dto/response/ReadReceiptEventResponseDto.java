package uz.shuhrat.lms.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReadReceiptEventResponseDto(
        UUID conversationId,
        UUID userId,
        String username,
        UUID otherLastReadMessageId,
        Instant readAt
) {}

