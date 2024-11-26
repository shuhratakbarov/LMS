package uz.farobiy.lms.db.customDto.admin;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {
    private String title;
    private String suffix;
    private int value;
}
