package uz.farobiy.lesson_11_backend.db.repository.file;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.farobiy.lesson_11_backend.db.domain.attachment.AttachmentContent;


import java.util.UUID;

@Repository
public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, UUID> {
}

