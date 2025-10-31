package uz.shuhrat.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.shuhrat.lms.enums.ExamStatus;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamDetailsDto {
    private Long id;
    private String topic;
    private String description;
    private ExamStatus status;
    private String difficulty;
    private List<String> areas;
    private Integer duration;
    private Instant deadline;
    private Long courseId;
    private String courseName;
    private Instant createdAt;
    private List<QuestionWithAnswerDto> questions;
}
