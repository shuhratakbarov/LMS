package uz.shuhrat.lms.db.customDto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.dto.GroupResponseDto;
import uz.shuhrat.lms.dto.PageDataResponseDto;

@Getter
@Setter
@AllArgsConstructor
public class GroupDataDto {
    private PageDataResponseDto<Page<UserCustomDtoForAdmin>> pageDataResponseDto;
    private Group group;
}
