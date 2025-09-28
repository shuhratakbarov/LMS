package uz.shuhrat.lms.db.customDto.teacher;

public interface LessonScheduleDTO {
    String getGroupName();

    Integer getDay();

    Integer getStartTime();

    Integer getEndTime();

    String getRoomName();
}
