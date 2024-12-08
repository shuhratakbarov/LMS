package uz.shuhrat.lms.db.repository.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.File;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByPkey(String s);
}
