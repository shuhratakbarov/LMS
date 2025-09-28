package uz.shuhrat.lms.db.customDto.teacher;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

public interface TaskListWithGroupNameProjection {
    UUID getId();

    Date getDeadline();

    BigDecimal getMaxBall();

    String getTaskName();

    Date getCreatedAt();
    Date getUpdatedAt();

    String getType();

    String getPkey();

    String getFileName();

    Long getSize();

    String getGroupName();
}
