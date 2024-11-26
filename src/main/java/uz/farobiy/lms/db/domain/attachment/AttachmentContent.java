package uz.farobiy.lms.db.domain.attachment;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.GenericGenerator;


import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AttachmentContent {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID pkey;
    private String contentType;
    private byte[] bytes;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;
}
