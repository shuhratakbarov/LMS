package uz.shuhrat.lms.db.domain.attachment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID pkey;
    private String name;
    private Long size;
    @OneToOne(mappedBy = "attachment", fetch = FetchType.LAZY)
    private AttachmentContent content;
}
