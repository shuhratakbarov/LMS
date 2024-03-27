package uz.farobiy.lesson_11_backend.db.domain.attachment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "pkey", updatable = false, nullable = false)
    private UUID pkey;
    private String name;
    private Long size;
    @OneToOne(mappedBy = "attachment",fetch = FetchType.LAZY)
    private AttachmentContent content;

}
