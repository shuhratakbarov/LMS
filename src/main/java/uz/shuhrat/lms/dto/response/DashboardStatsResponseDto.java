package uz.shuhrat.lms.db.customDto.admin;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponseDto {
    private String title;
    private String suffix;
    private int value;
}
