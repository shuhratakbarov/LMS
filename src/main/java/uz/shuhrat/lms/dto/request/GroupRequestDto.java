package uz.shuhrat.lms.dto.request;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public record GroupRequestDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank Long courseId,
        @NotBlank UUID teacherId
) {
}