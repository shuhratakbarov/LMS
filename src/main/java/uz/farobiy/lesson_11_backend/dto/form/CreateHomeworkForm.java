package uz.farobiy.lesson_11_backend.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHomeworkForm {
    private BigDecimal Ball;
    private UUID taskId;
    private UUID studentId;
}
