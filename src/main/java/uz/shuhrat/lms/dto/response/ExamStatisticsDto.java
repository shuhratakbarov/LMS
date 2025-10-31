package uz.shuhrat.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStatisticsDto {
    private Long examId;
    private Long totalStarted;
    private Long totalSubmitted;
    private Double averageScore;
    private Double highestScore;
    private Double lowestScore;
}

