package uz.shuhrat.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubmissionDto {
    private Long attendanceId;
    private UUID studentId;
    private String studentName;
    private Instant startedAt;
    private Instant submittedAt;
    private Double score;
}
