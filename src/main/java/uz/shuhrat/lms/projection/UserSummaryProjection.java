package uz.shuhrat.lms.projection;

import java.util.UUID;

public interface UserSummaryProjection {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getUsername();
}