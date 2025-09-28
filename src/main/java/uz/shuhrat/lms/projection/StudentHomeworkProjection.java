package uz.shuhrat.lms.projection;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

public interface StudentHomeworkProjection {
    UUID getTaskId();

    BigDecimal getMaxBall();

    LocalDateTime getDeadline();

    String getTaskFileName();

    String getTaskFileId();

    String getTaskName();

    String getType();

    Long getTaskFileSize();

    String getTaskPathUrl();

    UUID getHomeworkId();

    BigDecimal getHomeworkBall();

    String getDescription();

    String getHomeworkFileName();

    String getHomeworkPathUrl();

    String getHomeworkFileId();

    Long getHomeworkFileSize();

}