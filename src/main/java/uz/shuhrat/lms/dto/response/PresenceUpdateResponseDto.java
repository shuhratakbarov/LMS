package uz.shuhrat.lms.dto.response;

import java.time.Instant;

public record PresenceUpdateResponseDto(String username, boolean isOnline, Instant lastSeen) {}

