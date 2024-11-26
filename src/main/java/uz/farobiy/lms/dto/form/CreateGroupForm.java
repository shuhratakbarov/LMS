package uz.farobiy.lms.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupForm {
    private String name;
    private String description;
    private Long courseId;
    private UUID teacherId;
}
