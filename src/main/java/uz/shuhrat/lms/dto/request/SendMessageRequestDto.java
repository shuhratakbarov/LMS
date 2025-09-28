package uz.shuhrat.lms.dto.request;

import uz.shuhrat.lms.db.domain.enums.MessageType;

import java.util.UUID;

public record SendMessageRequest(
        UUID conversationId,
        String content,
        MessageType messageType
) {}


