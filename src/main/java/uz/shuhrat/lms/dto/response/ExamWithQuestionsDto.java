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
public class ExamWithQuestionsDto {
    private Long id;
    private String topic;
    private String description;
    private Integer duration;
    private Instant deadline;
    private Long attendanceId;
    private Instant startedAt;
    private List<QuestionDto> questions;
}
