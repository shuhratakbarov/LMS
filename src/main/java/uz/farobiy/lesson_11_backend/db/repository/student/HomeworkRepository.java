package uz.farobiy.lesson_11_backend.db.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.farobiy.lesson_11_backend.db.domain.tasks.Homework;

import java.util.UUID;
@Repository
public interface HomeworkRepository extends JpaRepository<Homework, UUID> {
}
