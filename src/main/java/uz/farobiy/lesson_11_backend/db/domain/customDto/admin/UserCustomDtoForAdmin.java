package uz.farobiy.lesson_11_backend.db.domain.customDto.admin;

import java.util.UUID;

public interface UserCustomDtoForAdmin {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getUsername();
}
