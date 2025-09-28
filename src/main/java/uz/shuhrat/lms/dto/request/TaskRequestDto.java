package uz.shuhrat.lms.dto.form;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Date;

public record TaskRequestDto(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank Date deadline,
        @NotBlank BigDecimal maxBall,
        @NotBlank String groupId
) {
}