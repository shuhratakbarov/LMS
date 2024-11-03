package uz.farobiy.lms_clone.db.repository.file;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.farobiy.lms_clone.db.domain.attachment.AttachmentContent;


import java.util.UUID;

@Repository
public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, UUID> {
}

