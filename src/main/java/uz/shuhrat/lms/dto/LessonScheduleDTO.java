package uz.shuhrat.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonScheduleDTO {
    private Long id;
    private Long groupId;
    private String groupName;
    private Integer day;
    private Integer startTime;
    private Integer endTime;
    private Long roomId;
    private String roomName;

    public LessonScheduleDTO(Long groupId, Integer day, Integer startTime, Integer endTime, Long roomId) {
        this.groupId = groupId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
    }
    public LessonScheduleDTO(String groupName, Integer day, Integer startTime, Integer endTime, String roomName) {
        this.groupName = groupName;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
    }
}
