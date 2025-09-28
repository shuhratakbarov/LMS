package uz.shuhrat.lms.dto.request;

import java.math.BigDecimal;

public record EvaluateHomeworkRequestDto(
        BigDecimal homeworkBall,
        String description
) {
}