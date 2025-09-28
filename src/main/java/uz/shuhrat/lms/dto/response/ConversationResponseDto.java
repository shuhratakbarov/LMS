package uz.shuhrat.lms.dto.response;

import uz.shuhrat.lms.db.domain.enums.ConversationOriginType;

import java.time.Instant;
import java.util.UUID;

public record ConversationListDto(
        UUID id,
        String name,
        ConversationOriginType originType,
        boolean isGroup,
        String role, // for users
        Long lmsGroupId, // null, if it's not SYSTEM group
        Instant lastSeen, // for users
        long unreadCount,
        String lastMessagePreview,
        Instant lastMessageCreatedAt,
        String lastMessageSenderUsername
) {}
