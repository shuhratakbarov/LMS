package uz.shuhrat.lms.projection;

import uz.shuhrat.lms.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

public interface LatestMessageProjection {
    UUID getConversationId();
    UUID getMessageId();
    String getContent();
    Instant getCreatedAt();
    String getSenderUsername();
     MessageType getMessageType();
}

