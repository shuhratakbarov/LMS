package uz.shuhrat.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamResponseDto {
    private Long attendanceId;
    private Instant submittedAt;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Double score;
}
