package uz.shuhrat.lms.dto.response;

import org.springframework.data.domain.Page;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.projection.UserSummaryProjection;

public record GroupDataResponseDto(
        Page<UserSummaryProjection> page,
        Group group
) {
}