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
public class InProgressExamDto {
    private Long attendanceId;
    private Long examId;
    private String examTopic;
    private String courseName;
    private Instant startedAt;
    private Integer duration;
    private Instant deadline;
}
