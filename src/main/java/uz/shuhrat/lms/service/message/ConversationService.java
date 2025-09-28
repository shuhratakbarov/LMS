package uz.shuhrat.lms.service.message;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.Conversation;
import uz.shuhrat.lms.db.domain.ConversationParticipant;
import uz.shuhrat.lms.db.domain.Message;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.enums.ConversationOriginType;
import uz.shuhrat.lms.enums.ConversationRole;
import uz.shuhrat.lms.enums.MessageType;
import uz.shuhrat.lms.db.repository.message.ConversationParticipantRepository;
import uz.shuhrat.lms.db.repository.message.ConversationRepository;
import uz.shuhrat.lms.db.repository.message.MessageRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.request.CreateConversationRequestDto;
import uz.shuhrat.lms.dto.request.SendMessageRequestDto;
import uz.shuhrat.lms.dto.response.*;
import uz.shuhrat.lms.projection.ConversationSearchProjection;
import uz.shuhrat.lms.projection.LatestMessageProjection;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationParticipantRepository conversationParticipantRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getUserConversations(UUID userId) {
        // Step 1 — Fetch conversations
        List<Conversation> conversations = conversationRepository.findAllByUserId(userId);
        List<UUID> conversationIds = conversations.stream()
                .map(Conversation::getId)
                .toList();

        // Step 2 — Fetch unread counts
        Map<UUID, Long> unreadMap = messageRepository.countUnreadMessagesByUser(userId).stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (Long) row[1]
                ));

        // Step 3 — Fetch latest messages with projection
        Map<UUID, LatestMessageProjection> latestMap =
                messageRepository.findLatestMessagesByConversations(conversationIds).stream()
                        .collect(Collectors.toMap(
                                LatestMessageProjection::getConversationId,
                                proj -> proj
                        ));

        // Step 4 — Map to DTO
        return conversations.stream()
                .map(conversation -> {
                    String name;
                    String username = null;
                    Instant lastSeen = null;
                    String role = null;

                    // only direct conversations supported
                    User otherUser = conversation.getParticipants().stream()
                            .map(ConversationParticipant::getUser)
                            .filter(user -> !user.getId().equals(userId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("No other participant found"));

                    name = otherUser.getFirstName() + " " + otherUser.getLastName();
                    username = otherUser.getUsername();
                    lastSeen = otherUser.getLastSeen();
                    role = otherUser.getRole().name();

                    long unreadCount = unreadMap.getOrDefault(conversation.getId(), 0L);

                    // Latest message info
                    UUID lastMessageId = null;
                    String lastMessagePreview = null;
                    Instant lastMessageCreatedAt = null;
                    String lastMessageSenderUsername = null;
                    boolean isRead = true; // default: true if no messages

                    LatestMessageProjection latest = latestMap.get(conversation.getId());
                    if (latest != null) {
                        lastMessageId = latest.getMessageId();
                        String content = latest.getContent();
                        lastMessageCreatedAt = latest.getCreatedAt();
                        lastMessageSenderUsername = latest.getSenderUsername();
                        MessageType type = latest.getMessageType();

                        if (MessageType.FILE.equals(type)) {
                            lastMessagePreview = "📎 File";
                        } else if (MessageType.IMAGE.equals(type)) {
                            lastMessagePreview = "🖼️ Photo";
                        } else {
                            lastMessagePreview = content != null && content.length() > 40
                                    ? content.substring(0, 40) + "..."
                                    : content != null ? content : "History was cleared";
                        }

                        // --- 👇 Read check based on OTHER participant ---
                        ConversationParticipant otherParticipant = conversation.getParticipants().stream()
                                .filter(p -> !p.getUser().getId().equals(userId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("No other participant found"));

                        if (lastMessageCreatedAt != null) {
                            Message otherLastReadMessage = otherParticipant.getLastReadMessage();
                            if (otherLastReadMessage != null) {
                                isRead = otherLastReadMessage.getCreatedAt().isAfter(lastMessageCreatedAt)
                                         || otherLastReadMessage.getCreatedAt().equals(lastMessageCreatedAt);
                            } else {
                                isRead = false;
                            }
                        }
                    }

                    return new ConversationResponseDto(
                            conversation.getId(),
                            name,
                            username,
                            conversation.getOriginType(),
                            conversation.isGroup(),
                            role,
                            conversation.getGroup() != null ? conversation.getGroup().getId() : null,
                            lastSeen,
                            unreadCount,
                            lastMessageId,
                            lastMessagePreview,
                            lastMessageCreatedAt,
                            lastMessageSenderUsername,
                            isRead
                    );
                })
                .sorted(Comparator.comparing(
                        ConversationResponseDto::lastMessageCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public MessagePageDto getConversationMessages(UUID conversationId, Pageable pageable, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ConversationParticipant self = conversationParticipantRepository
                .findByConversationIdAndUserId(conversationId, currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        UUID selfLastReadMessageId = self.getLastReadMessage() != null
                ? self.getLastReadMessage().getId()
                : null;

        ConversationParticipant other = conversationParticipantRepository
                .findByConversationIdAndUserIdNot(conversationId, currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Other participant not found"));

        UUID otherLastReadMessageId = null;
        if (other.getLastReadMessage() != null) {
            otherLastReadMessageId = other.getLastReadMessage().getId();
        }

        Page<MessageSummaryDto> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(m -> new MessageSummaryDto(
                        m.getId(),
                        conversationId,
                        m.getSender().getId(),
                        m.getSender().getUsername(),
                        m.getContent(),
                        m.getMessageType(),
                        m.getCreatedAt(),
                        ""
                ));
        return new MessagePageDto(messages, selfLastReadMessageId, otherLastReadMessageId);
    }

    @Transactional
    public ConversationResponseDto createConversation(
            UUID creatorId,
            CreateConversationRequestDto request
    ) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<User> participants = userRepository.findAllById(request.participantIds());
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("Participants cannot be empty");
        }

        Conversation conversation = Conversation.builder()
                .isGroup(request.isGroup())
                .name(request.isGroup() ? request.name() : null)
                .username(request.isGroup() ? request.username() : null)
                .originType(ConversationOriginType.USER_CREATED)
                .createdBy(creator)
                .createdAt(Instant.now())
                .build();

        Conversation savedConversation = conversationRepository.save(conversation);

        List<ConversationParticipant> participantEntities = new ArrayList<>();

        participantEntities.add(ConversationParticipant.builder()
                .conversation(conversation)
                .user(creator)
                .role(ConversationRole.ADMIN)
                .build());

        for (User user : participants) {
            participantEntities.add(ConversationParticipant.builder()
                    .conversation(conversation)
                    .user(user)
                    .role(ConversationRole.MEMBER)
                    .build());
        }

        conversationParticipantRepository.saveAll(participantEntities);

        if (conversation.isGroup()) {
            return new ConversationResponseDto(
                    savedConversation.getId(),
                    savedConversation.getName(),
                    savedConversation.getUsername(),
                    conversation.getOriginType(),
                    true,
                    "",
                    conversation.getGroup() != null ? conversation.getGroup().getId() : null,
                    null,
                    0,
                    null,
                    "",
                    null,
                    "",
                    false
            );
        } else {
            User otherUser = participants.stream()
                    .filter(p -> !p.getId().equals(creatorId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No other participant found"));
            return new ConversationResponseDto(
                    savedConversation.getId(),
                    otherUser.getFirstName() + " " + otherUser.getLastName(),
                    otherUser.getUsername(),
                    conversation.getOriginType(),
                    false,
                    otherUser.getRole().name(),
                    null,
                    otherUser.getLastSeen(),
                    0,
                    null,
                    "",
                    Instant.now(),
                    "",
                    false
            );
        }
    }


    @Transactional
    public Page<MessageSummaryDto> joinAndFetchMessages(UUID conversationId, UUID userId) throws Exception {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new Exception("Conversation not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (!conversation.isGroup()) {
            throw new IllegalArgumentException("Only group conversations can be joined");
        }

        Pageable pageable = PageRequest.of(0, 100, Sort.by("id").descending());
        boolean alreadyJoined = conversationParticipantRepository.existsByConversationIdAndUserId(conversationId, userId);
        if (!alreadyJoined) {
            ConversationParticipant participant = ConversationParticipant.builder()
                    .conversation(conversation)
                    .user(user)
                    .build(); // add last read message id
            conversationParticipantRepository.save(participant);
        }

        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(m -> new MessageSummaryDto(
                        m.getId(),
                        conversationId,
                        m.getSender().getId(),
                        m.getSender().getUsername(),
                        m.getContent(),
                        m.getMessageType(),
                        m.getCreatedAt(),
                        ""
                ));
    }

    public List<ReadReceiptDto> getReadReceipts(UUID conversationId) {
        List<ConversationParticipant> participants = conversationParticipantRepository
                .findAllByConversationId(conversationId);

        return participants.stream()
                .filter(p -> p.getLastReadMessage() != null)
                .map(p -> new ReadReceiptDto(
                        p.getUser().getId(),
                        p.getUser().getUsername(),
                        p.getLastReadMessage().getId(),
                        p.getLastReadMessage().getCreatedAt()
                ))
                .toList();
    }

    public List<ConversationSearchResultDto> searchConversations(String term, String username, String type) {
        if (term == null || term.trim().length() < 3) {
            return Collections.emptyList();
        }

        List<ConversationSearchProjection> results;

        if ("user".equalsIgnoreCase(type)) {
            return conversationRepository.searchPotentialUsers(term, username).stream()
                    .map(r -> new ConversationSearchResultDto(
                            UUID.fromString(r.getId()),
                            r.getName(),
                            r.getUsername(),
                            ConversationOriginType.USER_CREATED,
                            false,
                            r.getRole()
                    ))
                    .toList();
        } else if ("group".equalsIgnoreCase(type)) {
            results = conversationRepository.searchPotentialGroups(term, username);
            return results.stream()
                    .map(r -> new ConversationSearchResultDto(
                            UUID.fromString(r.getId()),
                            r.getName(),
                            r.getUsername(),
                            ConversationOriginType.USER_CREATED,
                            true,
                            null
                    ))
                    .toList();
        } else {
            throw new IllegalArgumentException("Invalid search type: " + type);
        }
    }

    @Transactional
    public MessageSummaryDto saveMessage(String username, SendMessageRequestDto request) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.content())
                .createdAt(Instant.now())
                .messageType(request.messageType())
                .build();

        Message saved = messageRepository.save(message);
        ConversationParticipant participant = conversationParticipantRepository
                .findByConversationIdAndUserId(conversation.getId(), sender.getId())
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        participant.setLastReadMessage(saved);
        conversationParticipantRepository.save(participant);

        // send WebSocket "read receipt" event
        // readReceiptPublisher.publish(conversation.getId(), sender.getUsername(), saved.getId());

        return new MessageSummaryDto(
                saved.getId(),
                conversation.getId(),
                sender.getId(),
                sender.getUsername(),
                saved.getContent(),
                saved.getMessageType(),
                saved.getCreatedAt(),
                request.tempId()
        );
    }

    public ReadReceiptEventResponseDto markAsRead(String username, UUID conversationId, UUID selfLastReadMessageId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ConversationParticipant self = conversationParticipantRepository
                .findByConversationIdAndUserId(conversationId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        Message message = messageRepository.findById(selfLastReadMessageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        Message currentLastRead = self.getLastReadMessage();

        // ✅ Idempotency check
        if (currentLastRead != null) {
            boolean isNewer = message.getCreatedAt().isAfter(currentLastRead.getCreatedAt()) ||
                              (message.getCreatedAt().equals(currentLastRead.getCreatedAt())
                               && message.getId().compareTo(currentLastRead.getId()) > 0);
            if (!isNewer) return null;
        }

        self.setLastReadMessage(message);

        conversationParticipantRepository.save(self);

        return new ReadReceiptEventResponseDto(
                conversationId,
                user.getId(),
                user.getUsername(),
                selfLastReadMessageId,
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    public Conversation findById(UUID conversationId) {
        return conversationRepository.findByIdWithParticipants(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
    }

    public Set<String> getUsersWithConversationsContaining(String username) {
        return conversationParticipantRepository.findUsersWithDirectConversationsContaining(username);
    }

    @Transactional(readOnly = true)
    public ConversationResponseDto getConversationById(UUID conversationId, UUID userId) {
        // Step 1 — Fetch conversation
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        // Step 2 — Unread count
        long unreadCount = messageRepository.countUnreadMessages(conversationId, userId);

        // Step 3 — Latest message
        Object[] latest = messageRepository.findLatestMessageByConversation(conversationId).orElse(null);

        String name;
        String username = null;
        Instant lastSeen = null;
        String role = null;

        if (conversation.isGroup()) {
            name = conversation.getName();
        } else {
            User otherUser = conversation.getParticipants().stream()
                    .map(ConversationParticipant::getUser)
                    .filter(user -> !user.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No other participant found"));

            name = otherUser.getFirstName() + " " + otherUser.getLastName();
            username = otherUser.getUsername();
            lastSeen = otherUser.getLastSeen();
            role = otherUser.getRole().name();
        }

        // Latest message info
        UUID lastMessageId = null;
        String lastMessagePreview = null;
        Instant lastMessageCreatedAt = null;
        String lastMessageSenderUsername = null;

        if (latest != null) {
            lastMessageId = (UUID) latest[1];
            String content = (String) latest[2];
            lastMessageCreatedAt = (Instant) latest[3];
            lastMessageSenderUsername = (String) latest[4];
            MessageType type = (MessageType) latest[5];

            if (MessageType.FILE.equals(type)) {
                lastMessagePreview = "📎 File";
            } else if (MessageType.IMAGE.equals(type)) {
                lastMessagePreview = "🖼️ Photo";
            } else {
                lastMessagePreview = content != null && content.length() > 40
                        ? content.substring(0, 40) + "..."
                        : content;
            }
        }

        return new ConversationResponseDto(
                conversation.getId(),
                name,
                username,
                conversation.getOriginType(),
                conversation.isGroup(),
                role,
                conversation.getGroup() != null ? conversation.getGroup().getId() : null,
                lastSeen,
                unreadCount,
                lastMessageId,
                lastMessagePreview,
                lastMessageCreatedAt,
                lastMessageSenderUsername,
                false
        );
    }
}
