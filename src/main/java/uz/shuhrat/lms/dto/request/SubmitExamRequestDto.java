package uz.shuhrat.lms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamRequestDto {
    @NotEmpty(message = "Answers are required")
    private List<AnswerRequestDto> answers;
}
