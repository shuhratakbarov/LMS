package uz.shuhrat.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDto {
    private Long questionId;
    private String question;
    private String selectedAnswer;
    private String correctAnswer;
    private boolean isCorrect;
}
