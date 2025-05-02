package uz.shuhrat.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarkAttendanceDTO {
    Long lessonInstanceId;
    List<AttendanceDTO> attendanceDTOS;
}
