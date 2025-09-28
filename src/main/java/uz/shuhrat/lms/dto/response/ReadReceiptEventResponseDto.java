package uz.shuhrat.lms.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReadReceiptEvent(
        UUID conversationId,
        UUID userId,
        String username,
        UUID lastReadMessageId,
        Instant readAt
) {}

