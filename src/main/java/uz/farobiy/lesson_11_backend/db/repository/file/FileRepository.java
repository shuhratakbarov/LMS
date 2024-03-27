package uz.farobiy.lesson_11_backend.db.repository.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.farobiy.lesson_11_backend.db.domain.File;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File,String> {

    Optional<File> findByPkey(String s);
}
