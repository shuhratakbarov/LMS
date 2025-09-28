package uz.shuhrat.lms.db.customDto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import uz.shuhrat.lms.db.domain.Group;

@Getter
@Setter
@AllArgsConstructor
public class GroupDataDto {
    private Page<UserCustomDtoForAdmin> page;
    private Group group;
}
