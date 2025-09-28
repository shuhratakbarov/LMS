package uz.shuhrat.lms.dto.response;

import java.util.UUID;

public record TypingEvent(
        UUID conversationId,
        UUID senderId,
        String senderUsername,
        boolean typing
) {}


