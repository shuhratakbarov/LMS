package uz.shuhrat.lms.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateHomework {
    private BigDecimal HomeworkBall;
    private String description;
}
