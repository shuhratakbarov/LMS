package uz.shuhrat.lms.component.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import uz.shuhrat.lms.component.jwt.JwtService;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.admin.TokenBlacklistRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistRepository blacklistRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Only check CONNECT frames
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("❌ WebSocket CONNECT failed: missing or invalid Authorization header");
                throw new IllegalArgumentException("No valid Authorization header");
            }

            String token = authHeader.substring(7);

            // Blacklist check
            if (blacklistRepository.findByToken(token).isPresent()) {
                log.warn("❌ WebSocket CONNECT failed: token is blacklisted");
                throw new IllegalArgumentException("Token is blacklisted");
            }

            try {
                String username = jwtService.extractUsername(token);
                if (username == null) {
                    throw new IllegalArgumentException("Invalid token");
                }

                User user = userRepository.findByUsername(username).orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

                if (!jwtService.validateToken(token, user)) {
                    throw new IllegalArgumentException("Token validation failed");
                }

                // Store info for later use (subscriptions, messaging, presence tracking)
                accessor.setUser(() -> username);
                accessor.getSessionAttributes().put("userId", user.getId().toString());
                accessor.getSessionAttributes().put("role", user.getRole().name());

                log.info("✅ WebSocket CONNECT success for user: {}", username);

            } catch (Exception e) {
                log.error("❌ WebSocket authentication error: {}", e.getMessage());
                throw new IllegalArgumentException("WebSocket authentication failed", e);
            }
        }

        return message;
    }
}

