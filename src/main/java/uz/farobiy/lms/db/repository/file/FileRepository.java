package uz.farobiy.lms.db.repository.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.farobiy.lms.db.domain.File;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByPkey(String s);
}
