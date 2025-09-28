package uz.shuhrat.lms.component.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import uz.shuhrat.lms.service.admin.UserService;
import uz.shuhrat.lms.service.message.PresenceService;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class PresenceEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;
    private final UserService userService;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = Optional.ofNullable(accessor.getUser())
                .map(Principal::getName)
                .orElse(null);

        if (username != null) {
            presenceService.userConnected(username);

            Set<String> onlineUsers = presenceService.getOnlineUsers();

            // ✅ Only send initial list privately to new user
            messagingTemplate.convertAndSendToUser(username, "/queue/online", onlineUsers);

            // ✅ Broadcast online users to everyone else
            messagingTemplate.convertAndSend("/topic/online", onlineUsers);

            log.info("✅ User connected: {}", username);
        } else {
            log.warn("⚠️ Username is null during SessionConnectedEvent");
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = Optional.ofNullable(accessor.getUser())
                .map(Principal::getName)
                .orElse(null);

        if (username != null) {
            presenceService.userDisconnected(username);

            Set<String> onlineUsers = presenceService.getOnlineUsers();

            messagingTemplate.convertAndSend("/topic/online", onlineUsers);

            userService.updateLastSeen(username);

            log.info("👋 User disconnected: {}", username);
        } else {
            log.warn("⚠️ Username is null during SessionDisconnectEvent");
        }
    }
}
