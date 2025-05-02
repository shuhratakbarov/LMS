package uz.shuhrat.lms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttendanceDTO {
    private UUID studentId;
    private Long lessonInstanceId;
    private boolean isPresent;
    private int minutesLate;
}