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
public class StudentExamHistoryDto {
    private Long attendanceId;
    private Long examId;
    private String examTopic;
    private String courseName;
    private Instant startedAt;
    private Instant submittedAt;
    private Double score;
}
