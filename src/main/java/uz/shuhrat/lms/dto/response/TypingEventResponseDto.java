package uz.shuhrat.lms.dto.response;

import java.util.UUID;

public record TypingEventResponseDto(
        UUID conversationId,
        UUID senderId,
        String senderUsername,
        boolean typing
) {
}