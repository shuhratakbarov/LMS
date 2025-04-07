package uz.shuhrat.lms.db.customDto.teacher;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

public interface HomeworkListDto {
    UUID getStudentId();

    String getFirstName();

    String getLastName();

    String getPhone();

    String getEmail();

    UUID getHomeworkId();

    BigDecimal getBall();

    String getDescription();

    Date getHomeworkCreatedAt();

    Date getHomeworkUpdatedAt();

    String getHomeworkFileId();

    String getHomeworkFileName();

    Long getHomeworkFileSize();
}
