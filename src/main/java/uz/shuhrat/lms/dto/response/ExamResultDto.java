package uz.shuhrat.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDto {
    private Long attendanceId;
    private String examTopic;
    private Instant startedAt;
    private Instant submittedAt;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Double score;
    private List<QuestionResultDto> questionResults;
}
