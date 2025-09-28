package uz.shuhrat.lms.db.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.Conversation;
import uz.shuhrat.lms.projection.ConversationSearchProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
    SELECT DISTINCT c
    FROM Conversation c
    JOIN c.participants p
    WHERE p.user.id = :userId
    """)
    List<Conversation> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.participants p LEFT JOIN FETCH p.user WHERE c.id = :id")
    Optional<Conversation> findByIdWithParticipants(@Param("id") UUID id);

    @Query(value = """
        SELECT
            CAST(u.id AS TEXT) AS id,
            CONCAT(u.first_name, ' ', u.last_name) AS name,
            u.username AS username,
            u.role AS role
        FROM users u
        WHERE (
            LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%'))
        )
          AND u.username <> :currentUsername
          AND NOT EXISTS (
              SELECT 1
              FROM conversation_participants p1
              JOIN conversations c ON p1.conversation_id = c.id
              JOIN conversation_participants p2 ON p2.conversation_id = c.id
              WHERE p1.user_id = (SELECT id FROM users WHERE username = :currentUsername)
                AND p2.user_id = u.id
                AND c.is_group = false
                AND c.origin_type <> 'SYSTEM'
          )
        ORDER BY 
            CASE 
                WHEN LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%')) THEN 1
                ELSE 2
            END,
            name
        LIMIT 10
    """, nativeQuery = true)
    List<ConversationSearchProjection> searchPotentialUsers(
            @Param("term") String term,
            @Param("currentUsername") String currentUsername
    );


    @Query(value = """
        SELECT
            CAST(c.id AS TEXT) AS id,
            c.name AS name,
            c.username
        FROM conversations c
        WHERE c.is_group = true
          AND c.origin_type <> 'SYSTEM'
          AND LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%'))
          AND NOT EXISTS (
              SELECT 1
              FROM conversation_participants p
              WHERE p.conversation_id = c.id
                AND p.user_id = (SELECT id FROM users WHERE username = :currentUsername)
          )
        ORDER BY name
        LIMIT 10
    """, nativeQuery = true)
    List<ConversationSearchProjection> searchPotentialGroups(
            @Param("term") String term,
            @Param("currentUsername") String currentUsername
    );

//    @Query(value = """
//    SELECT
//        c.id AS id,
//        c.is_group AS is_group,
//        c.name AS name,
//        CASE WHEN c.is_group = FALSE THEN u2.username ELSE c.username END AS username,
//        c.origin_type AS origin_type,
//        c.group_id AS group_id,
//        CASE WHEN c.is_group = FALSE THEN u2.last_seen ELSE NULL END AS last_seen
//    FROM conversations c
//    JOIN conversation_participants cp1 ON c.id = cp1.conversation_id
//    LEFT JOIN conversation_participants cp2 ON c.id = cp2.conversation_id AND cp2.user_id <> cp1.user_id
//    LEFT JOIN users u2 ON cp2.user_id = u2.id
//    WHERE cp1.user_id = (
//        SELECT id FROM users WHERE username = :currentUsername
//    )
//    AND (
//        LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%'))
//        OR LOWER(u2.username) LIKE LOWER(CONCAT('%', :term, '%'))
//    )
//
//    UNION
//
//    SELECT
//        u.id AS id,
//        FALSE AS is_group,
//        CONCAT(u.first_name, ' ', u.last_name) AS name,
//        u.username AS username,
//        'USER_CREATED' AS origin_type,
//        NULL AS group_id,
//        u.last_seen AS last_seen
//    FROM users u
//    WHERE (
//        LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%'))
//        OR LOWER(CONCAT(u.first_name, ' ', u.last_name)) LIKE LOWER(CONCAT('%', :term, '%'))
//    )
//    AND u.username <> :currentUsername
//    AND NOT EXISTS (
//        SELECT 1
//        FROM conversations c
//        JOIN conversation_participants cp1 ON c.id = cp1.conversation_id
//        JOIN conversation_participants cp2 ON c.id = cp2.conversation_id
//        WHERE c.is_group = FALSE
//        AND cp1.user_id = (SELECT id FROM users WHERE username = :currentUsername)
//        AND cp2.user_id = u.id
//    )
//""", nativeQuery = true)
//    List<Object[]> searchConversationsAndAvailableUsers(
//            @Param("term") String term,
//            @Param("currentUsername") String currentUsername

//    );


}

