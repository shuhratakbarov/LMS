package uz.shuhrat.lms.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupForm {
    private Long id;
    private String name;
    private String description;
    private Long courseId;
    private UUID teacherId;
}
