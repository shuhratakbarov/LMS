package uz.shuhrat.lms.dto.form;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskForm {
    private String name;
    private Date deadline;
    private BigDecimal maxBall;
    private Long groupId;
}
