package uz.farobiy.lesson_11_backend.dto.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskForm {
    private String name;
    private Date deadline;
    private BigDecimal maxBall;
    private Long groupId;
}
