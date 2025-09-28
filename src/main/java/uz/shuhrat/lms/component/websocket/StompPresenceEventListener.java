package uz.shuhrat.lms.component.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import uz.shuhrat.lms.dto.response.ConversationEventResponseDto;
import uz.shuhrat.lms.dto.response.PresenceUpdateResponseDto;
import uz.shuhrat.lms.service.admin.UserService;
import uz.shuhrat.lms.service.message.ConversationService;
import uz.shuhrat.lms.service.message.PresenceService;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import uz.shuhrat.lms.enums.ConversationEventType;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompPresenceEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;
    private final ConversationService conversationService;
    private final UserService userService;

    private String resolveUsername(StompHeaderAccessor accessor) {
        return Optional.ofNullable(accessor.getUser()).map(Principal::getName).orElseGet(() -> (String) accessor.getSessionAttributes().get("username"));
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = resolveUsername(accessor);
        if (username != null) {
            presenceService.userConnected(username);
            Set<String> onlineUsers = presenceService.getOnlineUsers();
            messagingTemplate.convertAndSendToUser(username, "/queue/online", onlineUsers);
            try {
                messagingTemplate.convertAndSend("/topic/online", onlineUsers);
            } catch (Exception e) {
                log.warn("⚠️ Could not send online users update, session already closed: {}", e.getMessage());
            }
            log.info("✅ User connected: {}", username);
        } else {
            log.warn("⚠️ Username is null during SessionConnectedEvent");
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = resolveUsername(accessor);
        if (username != null) {
            presenceService.userDisconnected(username);
            Set<String> onlineUsers = presenceService.getOnlineUsers();

            try {
                messagingTemplate.convertAndSend("/topic/online", onlineUsers);
            } catch (Exception e) {
                log.warn("⚠️ Could not send online users update, session already closed: {}", e.getMessage());
            }

            Instant lastSeen = userService.updateLastSeen(username);

            // Send PRESENCE event to users with conversations
            Set<String> usersToNotify = conversationService.getUsersWithConversationsContaining(username);
            PresenceUpdateResponseDto presenceUpdate = new PresenceUpdateResponseDto(username, false, lastSeen);

            for (String userToNotify : usersToNotify) {
                ConversationEventResponseDto responseEvent = new ConversationEventResponseDto(
                        ConversationEventType.PRESENCE,
                        null, // no specific conversation
                        presenceUpdate
                );
                try {
                    messagingTemplate.convertAndSend("/topic/user." + userToNotify + ".conversations", responseEvent);
                } catch (Exception e) {
                    log.warn("⚠️ Could not send presence update to {}: {}", userToNotify, e.getMessage());
                }
            }

            log.info("👋 User disconnected: {}", username);
        }
    }
}