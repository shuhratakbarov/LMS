package uz.shuhrat.lms.dto.response;

import uz.shuhrat.lms.enums.ConversationOriginType;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponseDto(
        UUID id,
        String name,
        String username,
        ConversationOriginType originType,
        boolean isGroup,
        String role, // for users
        Long lmsGroupId, // null, if it's not SYSTEM group
        Instant lastSeen, // for users
        long unreadCount,
        UUID lastMessageId,
        String lastMessagePreview,
        Instant lastMessageCreatedAt,
        String lastMessageSenderUsername,
        boolean isRead
) {}
