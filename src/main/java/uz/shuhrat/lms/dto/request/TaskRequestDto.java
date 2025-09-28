package uz.shuhrat.lms.dto.request;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record TaskRequestDto(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank LocalDateTime deadline,
        @NotBlank BigDecimal maxBall,
        @NotBlank String groupId
) {
}