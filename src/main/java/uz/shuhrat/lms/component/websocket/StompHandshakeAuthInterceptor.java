package uz.shuhrat.lms.component.websocket;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import uz.shuhrat.lms.component.jwt.JwtService;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.admin.TokenBlacklistRepository;
import uz.shuhrat.lms.db.repository.admin.UserRepository;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandshakeAuthInterceptor implements HandshakeInterceptor {
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

        if (blacklistRepository.findByToken(token).isPresent()) {
            log.warn("WebSocket handshake failed: Token is blacklisted");
            return false;
        }

        String username = jwtService.extractUsername(token);
        if (username == null) {
            log.warn("WebSocket handshake failed: Invalid token");
            return false;
        }

        UserDetails userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (userDetails == null || !jwtService.validateToken(token, userDetails)) {
            log.warn("WebSocket handshake failed: User not found or invalid token");
            return false;
        }
        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        attributes.put("principal", principal);

        log.info("WebSocket handshake successful for userDetails: {}", username);
        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("Handshake completed: {}", request.getURI());
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String paramToken = httpRequest.getParameter("token");
            if (paramToken != null && !paramToken.isEmpty()) {
                return paramToken;
            }
            String bearerToken = httpRequest.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }
}