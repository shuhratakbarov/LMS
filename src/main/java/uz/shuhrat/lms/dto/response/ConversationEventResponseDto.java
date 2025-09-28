package uz.shuhrat.lms.dto.response;

import uz.shuhrat.lms.enums.ConversationEventType;

import java.util.UUID;

public record ConversationEventResponseDto(
        ConversationEventType type,
        UUID conversationId,
        Object payload
) {
}
