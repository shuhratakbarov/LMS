package uz.shuhrat.lms.projection;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TaskListWithGroupNameProjection {
    UUID getId();

    LocalDateTime getDeadline();

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
