package uz.shuhrat.lms.db.repository.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.Message;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.projection.LatestMessageProjection;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Optional<Message> findFirstByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    Page<Message> findByConversationIdOrderByCreatedAtDesc(
            UUID conversationId,
            Pageable pageable
    );

    Message findTopByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    @Query("""
        SELECT m
        FROM Message m
        WHERE m.conversation.id = :conversationId
        ORDER BY m.createdAt DESC
    """)
    List<Message> findTop1ByConversationIdOrderByCreatedAtDesc(@Param("conversationId") UUID conversationId);

    int countByConversationIdAndCreatedAtAfter(UUID id, Instant lastRead);

    @Query("""
    SELECT c.id, COUNT(m)
    FROM Conversation c
    JOIN c.participants cp
    JOIN c.messages m
    WHERE cp.user.id = :userId
      AND (cp.lastReadMessage IS NULL OR m.createdAt > cp.lastReadMessage.createdAt)
    GROUP BY c.id
""")
    List<Object[]> countUnreadMessagesByUser(@Param("userId") UUID userId);


    @Query(value = """
    SELECT m.conversation_id AS conversationId,
           m.id AS messageId,
           m.content AS content,
           m.created_at AS createdAt,
           u.username AS senderUsername,
           m.message_type AS messageType
    FROM messages m
    JOIN users u ON m.sender_id = u.id
    JOIN (
        SELECT conversation_id, MAX(created_at) AS max_created_at
        FROM messages
        WHERE conversation_id IN (:conversationIds)
        GROUP BY conversation_id
    ) latest
      ON m.conversation_id = latest.conversation_id
     AND m.created_at = latest.max_created_at
    """, nativeQuery = true)
    List<LatestMessageProjection> findLatestMessagesByConversations(@Param("conversationIds") List<UUID> conversationIds);


    // --- Unread count for a single conversation ---
    @Query("""
    SELECT COUNT(m)
    FROM Conversation c
    JOIN c.participants cp
    LEFT JOIN cp.lastReadMessage lrm
    JOIN c.messages m
    WHERE cp.user.id = :userId
      AND c.id = :conversationId
      AND m.createdAt > COALESCE(lrm.createdAt, '1970-01-01T00:00:00Z')
""")
    long countUnreadMessages(
            @Param("userId") UUID userId,
            @Param("conversationId") UUID conversationId
    );
    // --- Latest message for a single conversation ---
    @Query("""
    SELECT m.id, m.content, m.createdAt, m.sender.username, m.messageType
    FROM Message m
    WHERE m.conversation.id = :conversationId
    ORDER BY m.createdAt DESC
    LIMIT 1
""")
    Optional<Object[]> findLatestMessageByConversation(
            @Param("conversationId") UUID conversationId
    );

    @Query("select m.id, m.createdAt from Message m where m.id in :ids")
    List<Object[]> findCreatedAtByIds(@Param("ids") Collection<UUID> ids);

}
