package uz.farobiy.lesson_11_backend.db.repository.file;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.farobiy.lesson_11_backend.db.domain.attachment.Attachment;


import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
}
