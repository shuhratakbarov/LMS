package uz.shuhrat.lms.component.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import uz.shuhrat.lms.component.jwt.JwtService;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.admin.TokenBlacklistRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistRepository blacklistRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String token = extractTokenFromRequest(request);
        if (token == null) {
            log.warn("WebSocket handshake failed: No token provided");
            return false;
        }

        try {
            // Blacklist check
            if (blacklistRepository.findByToken(token).isPresent()) {
                log.warn("WebSocket handshake failed: Token is blacklisted");
                return false;
            }

            String username = jwtService.extractUsername(token);
            if (username == null) {
                log.warn("WebSocket handshake failed: Invalid token");
                return false;
            }

            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.warn("WebSocket handshake failed: User not found");
                return false;
            }

            if (!jwtService.validateToken(token, user)) {
                log.warn("WebSocket handshake failed: Token validation failed");
                return false;
            }

            // Store attributes for ChannelInterceptor
            attributes.put("username", username);
            attributes.put("userId", user.getId().toString());
            attributes.put("role", user.getRole().name());

            log.info("WebSocket handshake successful for user: {}", username);
            return true;

        } catch (Exception e) {
            log.error("WebSocket handshake error: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("Handshake completed: {}", request.getURI());
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            List<String> headers = servletRequest.getServletRequest().getHeaders("Authorization") != null
                    ? List.of(servletRequest.getServletRequest().getHeaders("Authorization").nextElement())
                    : null;

            if (headers != null && !headers.isEmpty()) {
                String bearerToken = headers.get(0);
                if (bearerToken.startsWith("Bearer ")) {
                    return bearerToken.substring(7);
                }
            }
        }
        return null;
    }
}