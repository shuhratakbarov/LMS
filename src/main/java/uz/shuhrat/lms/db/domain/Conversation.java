package uz.shuhrat.lms.db.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import uz.shuhrat.lms.enums.ConversationOriginType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(nullable = false)
    private boolean isGroup;

    @Column(unique = true)
    private String username;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationOriginType originType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id") // Nullable: LMS group if applicable
    private Group group;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConversationParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();
}
