package uz.shuhrat.lms.db.customDto.admin;

import java.util.UUID;

public interface UserSummaryProjection {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getUsername();
}
