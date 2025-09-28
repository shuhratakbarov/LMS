package uz.shuhrat.lms.controller.websocket;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.message.DirectMessageRequestDto;
import uz.shuhrat.lms.dto.message.DirectMessageResponseDto;
import uz.shuhrat.lms.dto.request.MarkAsReadRequest;
import uz.shuhrat.lms.dto.request.SendMessageRequest;
import uz.shuhrat.lms.dto.request.TypingEventRequest;
import uz.shuhrat.lms.dto.response.MessageSummaryDto;
import uz.shuhrat.lms.dto.response.ReadReceiptEvent;
import uz.shuhrat.lms.dto.response.TypingEvent;
import uz.shuhrat.lms.service.message.ConversationService;
import uz.shuhrat.lms.service.message.DirectMessageService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConversationWebSocketController {

    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    /**
     * Client sends message to /app/conversation.sendMessage
     */
    @MessageMapping("/conversation.sendMessage")
    public void sendMessage(
            @Payload SendMessageRequest request,
            Principal principal
    ) {
        String username = principal.getName();
        MessageSummaryDto savedMessage = conversationService.saveMessage(username, request);

        // Broadcast to all participants
        messagingTemplate.convertAndSend(
                "/topic/conversation." + request.conversationId(),
                savedMessage
        );

        log.info("User {} sent message {} in conversation {}", username, savedMessage.id(), request.conversationId());
    }


    /**
     * Client sends read receipt to /app/conversation.markAsRead
     */
    @MessageMapping("/conversation.markAsRead")
    public void markAsRead(
            @Payload MarkAsReadRequest request,
            Principal principal
    ) {
        String username = principal.getName();

        ReadReceiptEvent event = conversationService.markAsRead(
                username,
                request.conversationId(),
                request.lastReadMessageId()
        );

        // Broadcast to all participants
        messagingTemplate.convertAndSend(
                "/topic/conversation." + request.conversationId() + ".readReceipts",
                event
        );

        log.info("User {} marked message {} as read in conversation {}", username, request.lastReadMessageId(), request.conversationId());
    }


    /**
     * Client sends typing notification to /app/conversation.typing
     */
    @MessageMapping("/conversation.typing")
    public void typing(
            @Payload TypingEventRequest request,
            Principal principal
    ) {
        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Broadcast to all participants
        messagingTemplate.convertAndSend(
                "/topic/conversation." + request.conversationId() + ".typing",
                new TypingEvent(
                        request.conversationId(),
                        user.getId(),
                        username
                )
        );

        log.info("User {} is typing in conversation {}", username, request.conversationId());
    }

}

