package uz.shuhrat.lms.dto.response;

import java.sql.Date;

public record GroupResponseDto(
        Long id,
        String name,
        String description,
        String courseName,
        String teacherUsername,
        Date createdAt,
        Date updatedAt
) {
}