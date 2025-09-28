package uz.shuhrat.lms.projection;

public interface LessonScheduleProjection {
    String getGroupName();

    Integer getDay();

    Integer getStartTime();

    Integer getEndTime();

    String getRoomName();
    String getCourseName();
}
