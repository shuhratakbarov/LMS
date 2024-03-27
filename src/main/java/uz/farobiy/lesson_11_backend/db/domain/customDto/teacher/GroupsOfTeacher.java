package uz.farobiy.lesson_11_backend.db.domain.customDto.teacher;

import uz.farobiy.lesson_11_backend.db.domain.tasks.costomDto.TaskCustomDtoForTeacher;

import java.util.UUID;

public interface GroupsOfTeacher {
    UUID getId ();
    String getName();
    String getCourseName();
    String getUsername();
    TaskCustomDtoForTeacher getTask();
}
