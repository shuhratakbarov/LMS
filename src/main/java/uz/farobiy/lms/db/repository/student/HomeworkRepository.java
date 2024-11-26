package uz.farobiy.lms.db.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.farobiy.lms.db.domain.Homework;

import java.util.UUID;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, UUID> {
}
