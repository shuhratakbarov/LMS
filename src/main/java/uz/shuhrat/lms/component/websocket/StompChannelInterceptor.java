package uz.shuhrat.lms.component.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Principal user = accessor.getUser();
            if (user == null) {
                log.warn("❌ WebSocket CONNECT failed: no Principal");
                throw new IllegalArgumentException("No authenticated user found in CONNECT frame");
            }

            log.info("✅ WebSocket CONNECT success for user: {}", user.getName());
        }

        return message;
    }
}
