package uz.shuhrat.lms.dto;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDto {
    private Long id;
    private String name;
    private String description;
    private String courseName;
    private String teacherUsername;
    private Date createdAt;
    private Date updatedAt;
}
