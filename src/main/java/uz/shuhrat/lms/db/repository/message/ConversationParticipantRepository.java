package uz.shuhrat.lms.db.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.db.domain.ConversationParticipant;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, UUID> {
    Optional<ConversationParticipant> findByConversationIdAndUserId(UUID conversationId, UUID userId);

    Optional<ConversationParticipant> findByConversationIdAndUserIdNot(UUID conversationId, UUID id);

    List<ConversationParticipant> findAllByConversationId(UUID conversationId);

    boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);

    @Query("""
        SELECT DISTINCT cp2.user.username
        FROM ConversationParticipant cp1
        JOIN ConversationParticipant cp2 ON cp1.conversation.id = cp2.conversation.id
        WHERE cp1.user.username = :username
        AND cp2.user.username != :username
        AND cp1.conversation.isGroup = false
        """)
    Set<String> findUsersWithDirectConversationsContaining(@Param("username") String username);


}
