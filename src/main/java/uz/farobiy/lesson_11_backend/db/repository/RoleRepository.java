package uz.farobiy.lesson_11_backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.farobiy.lesson_11_backend.db.domain.Role;
import uz.farobiy.lesson_11_backend.db.domain.customDto.teacher.GroupCustomForTeacher;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);


}
