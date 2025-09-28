package uz.shuhrat.lms.dto.request;

import java.util.UUID;

public record MarkAsReadRequest(
        UUID conversationId,
        UUID lastReadMessageId
) {}

