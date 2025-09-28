package uz.shuhrat.lms.dto.request;

import uz.shuhrat.lms.enums.MessageType;

import java.util.UUID;

public record SendMessageRequestDto(
        UUID conversationId,
        String tempId,
        String content,
        MessageType messageType
) {
}