package uz.shuhrat.lms.dto.request;

import java.util.UUID;

public record TypingEventRequest(
        UUID conversationId
) {}

