package uz.farobiy.lms_clone.db.customDto.admin;

import java.util.UUID;

public interface UserCustomDtoForAdmin {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getUsername();
}
