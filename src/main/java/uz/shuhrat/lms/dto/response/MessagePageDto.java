package uz.shuhrat.lms.dto.response;

import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.UUID;

public record MessagePageDto(
        Page<MessageSummaryDto> messages,
        UUID selfLastReadMessageId,
        UUID otherLastReadMessageId
) {}

