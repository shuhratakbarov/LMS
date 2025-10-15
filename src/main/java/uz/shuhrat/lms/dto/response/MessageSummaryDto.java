package uz.shuhrat.lms.dto.response;

import uz.shuhrat.lms.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageSummaryDto(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String senderUsername,
        String content,
        MessageType messageType,
        Instant createdAt,
        String tempId
) {}

