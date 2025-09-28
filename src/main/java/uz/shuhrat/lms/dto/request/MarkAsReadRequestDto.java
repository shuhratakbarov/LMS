package uz.shuhrat.lms.dto.request;

import java.util.UUID;

public record MarkAsReadRequestDto(
        UUID conversationId,
        UUID selfLastReadMessageId
) {
}