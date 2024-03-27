package uz.farobiy.lesson_11_backend.db.domain.tasks.costomDto;

import uz.farobiy.lesson_11_backend.db.domain.attachment.Attachment;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

public class TaskCustomDtoForTeacher {
    private UUID id;
    private String name;
    private UUID teacherId;
    private Date deadline;
    private BigDecimal maxBall;
    private Attachment attachment;
}
