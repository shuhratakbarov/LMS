package uz.shuhrat.lms.dto.request;

import javax.validation.constraints.NotBlank;

public record CourseRequestDto(
        @NotBlank String name,
        @NotBlank Integer duration,
        String description
) {
}
