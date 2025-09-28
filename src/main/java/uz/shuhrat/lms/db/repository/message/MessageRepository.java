package uz.shuhrat.lms.db.repository.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.DirectMessage;
import uz.shuhrat.lms.db.domain.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, UUID> {
    @Query("SELECT m FROM DirectMessage m WHERE " +
           "(m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.createdAt DESC")
    Page<DirectMessage> findDirectMessagesBetweenUsers(@Param("user1") User user1,
                                                 @Param("user2") User user2,
                                                 Pageable pageable);

    @Query("SELECT m FROM DirectMessage m WHERE m.receiver = :user AND m.isRead = false")
    List<DirectMessage> findUnreadMessages(@Param("user") User user);

    @Query("SELECT DISTINCT m.sender FROM DirectMessage m WHERE m.receiver = :user")
    List<User> findDirectMessageSenders(@Param("user") User user);

    @Query("SELECT DISTINCT m.receiver FROM DirectMessage m WHERE m.sender = :user")
    List<User> findDirectMessageReceivers(@Param("user") User user);

    @Query("SELECT COUNT(m) FROM DirectMessage m WHERE m.receiver = :user AND m.isRead = false")
    Long countUnreadMessages(@Param("user") User user);

    Page<DirectMessage> findByConversationId(UUID conversationId, Pageable pageable);


//    @Query("SELECT m FROM Message m WHERE m.roomId = :roomId AND m.messageScope = :type ORDER BY m.createdAt DESC")
//    Page<Message> findByRoomIdAndType(@Param("roomId") String roomId,
//                                      @Param("type") MessageScope type,
//                                      Pageable pageable);
}