package uz.shuhrat.lms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamRequestDto {
    @NotBlank(message = "Topic is required")
    private String topic;

    private String description;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    private List<String> areas;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    private Instant deadline;

    @NotEmpty(message = "At least one question is required")
    private List<QuestionRequestDto> questions;
}
