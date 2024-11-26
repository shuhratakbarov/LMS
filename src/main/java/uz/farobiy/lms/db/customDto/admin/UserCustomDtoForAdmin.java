package uz.farobiy.lms.db.customDto.admin;

import java.util.UUID;

public interface UserCustomDtoForAdmin {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getUsername();
}
