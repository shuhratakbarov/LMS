package uz.shuhrat.lms.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface HomeworkNotificationDetailsProjection {
    String getCourseName();

    String getGroupName();

    Long getGroupId();

    String getTaskName();

    String getTaskType();

    LocalDateTime getDeadline();

    Integer getDaysLeft();

    UUID getTaskId();

    String getPriority();

}
