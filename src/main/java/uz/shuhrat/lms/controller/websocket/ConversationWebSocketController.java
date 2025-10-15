package uz.shuhrat.lms.controller.websocket;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uz.shuhrat.lms.db.domain.Conversation;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.repository.admin.UserRepository;
import uz.shuhrat.lms.dto.request.MarkAsReadRequestDto;
import uz.shuhrat.lms.dto.request.SendMessageRequestDto;
import uz.shuhrat.lms.dto.request.TypingEventRequestDto;
import uz.shuhrat.lms.dto.response.ConversationEventResponseDto;
import uz.shuhrat.lms.dto.response.MessageSummaryDto;
import uz.shuhrat.lms.dto.response.ReadReceiptEventResponseDto;
import uz.shuhrat.lms.dto.response.TypingEventResponseDto;
import uz.shuhrat.lms.enums.ConversationEventType;
import uz.shuhrat.lms.service.message.ConversationService;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConversationWebSocketController {

    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/conversation.sendMessage")
    public void sendMessage(@Payload SendMessageRequestDto request, Principal principal) {
        String senderUsername = principal.getName();

        log.info("📩 User {} sent message tempId {} in conversation {}", senderUsername, request.tempId(), request.conversationId());

        MessageSummaryDto savedMessage = conversationService.saveMessage(senderUsername, request);
        Conversation conversation = conversationService.findById(request.conversationId());

        ConversationEventResponseDto event = new ConversationEventResponseDto(
                ConversationEventType.MESSAGE,
                request.conversationId(),
                savedMessage
        );

        // Send to all participants
        conversation.getParticipants().forEach(p ->
                messagingTemplate.convertAndSend(
                        returnDestination(p.getUser().getUsername()),
                        event
                )
        );

        log.info("📩 User {} sent message {} in conversation {}", senderUsername, savedMessage.id(), request.conversationId());
    }

    @MessageMapping("/conversation.markAsRead")
    public void markAsRead(@Payload MarkAsReadRequestDto request, Principal principal) {
        String username = principal.getName();

        ReadReceiptEventResponseDto receipt = conversationService.markAsRead(
                username,
                request.conversationId(),
                request.selfLastReadMessageId()
        );

        if (receipt == null) return;

        ConversationEventResponseDto event = new ConversationEventResponseDto(
                ConversationEventType.READ_RECEIPT,
                request.conversationId(),
                receipt
        );

        Conversation conversation = conversationService.findById(request.conversationId());
        conversation.getParticipants().forEach(p ->
                messagingTemplate.convertAndSend(
                        returnDestination(p.getUser().getUsername()),
                        event
                )
        );

        log.info("✅ User {} marked message {} as read in conversation {}", username, request.selfLastReadMessageId(), request.conversationId());
    }

    @MessageMapping("/conversation.typing.started")
    public void typingStarted(@Payload TypingEventRequestDto request, Principal principal) {
        sendTypingEvent(request, principal, true);
    }

    @MessageMapping("/conversation.typing.stopped")
    public void typingStopped(@Payload TypingEventRequestDto request, Principal principal) {
        sendTypingEvent(request, principal, false);
    }

    private void sendTypingEvent(TypingEventRequestDto request, Principal principal, boolean started) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        TypingEventResponseDto typing = new TypingEventResponseDto(
                request.conversationId(),
                user.getId(),
                username,
                started
        );

        ConversationEventResponseDto event = new ConversationEventResponseDto(
                ConversationEventType.TYPING,
                request.conversationId(),
                typing
        );

        Conversation conversation = conversationService.findById(request.conversationId());
        conversation.getParticipants().forEach(p -> {
            if (!p.getUser().getUsername().equals(username)) {
                messagingTemplate.convertAndSend(
                        returnDestination(p.getUser().getUsername()),
                        event
                );
            }
        });

//        log.info("⌨️ Typing event [{}] by {} in conversation {}", started ? "STARTED" : "STOPPED", username, request.conversationId());
    }

    private String returnDestination(String username) {
        return "/topic/user." + username + ".conversations";
    }
}



//@Slf4j
//@Controller
//@RequiredArgsConstructor
//public class ConversationWebSocketController {
//
//    private final ConversationService conversationService;
//    private final SimpMessagingTemplate messagingTemplate;
//    private final UserRepository userRepository;
//
//    @MessageMapping("/conversation.sendMessage")
//    public void sendMessage(
//            @Payload SendMessageRequestDto request,
//            Principal principal
//    ) {
//        String senderUsername = principal.getName();
//        MessageSummaryDto savedMessage = conversationService.saveMessage(senderUsername, request);
//
//        Conversation conversation = conversationService.findById(request.conversationId());
//        Set<ConversationParticipant> participants = conversation.getParticipants();
//
//        if (conversation.isGroup()) {
//            messagingTemplate.convertAndSend(
//                    "/topic/conversation." + request.conversationId(),
//                    savedMessage
//            );
//        } else {
//            for (ConversationParticipant participant : participants) {
//                String recipientUsername = participant.getUser().getUsername();
//                messagingTemplate.convertAndSendToUser(
//                        recipientUsername,
//                        "/queue/messages",
//                        savedMessage
//                );
//            }
//        }
//
//
//        log.info("User {} sent message {} in conversation {}", senderUsername, savedMessage.id(), request.conversationId());
//    }
//
//    @MessageMapping("/conversation.markAsRead")
//    public void markAsRead(@Payload MarkAsReadRequestDto request, Principal principal) {
//        log.info("Received markAsRead frame with payload: {}", request);
//        String username = principal.getName();
//
//        ReadReceiptEventResponseDto event = conversationService.markAsRead(
//                username,
//                request.conversationId(),
//                request.otherLastReadMessageId()
//        );
//
//        if (event == null) {
//            return;
//        }
//
//        String destination = "/topic/conversation." + request.conversationId() + ".readReceipts";
//        log.info("Sending read-receipt event to topic: {} | Event: {}", destination, event);
//
//        messagingTemplate.convertAndSend(destination, event);
//
//        log.info("User {} marked message {} as read in conversation {}",
//                username, request.otherLastReadMessageId(), request.conversationId());
//    }
//
//    @MessageMapping("/conversation.typing.started")
//    public void typingStarted(@Payload TypingEventRequestDto request, Principal principal) {
//        sendTypingEvent(request, principal, true);
//    }
//
//    @MessageMapping("/conversation.typing.stopped")
//    public void typingStopped(@Payload TypingEventRequestDto request, Principal principal) {
//        sendTypingEvent(request, principal, false);
//    }
//
//    private void sendTypingEvent(TypingEventRequestDto request, Principal principal, boolean started) {
//        String username = principal.getName();
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        TypingEventResponseDto event = new TypingEventResponseDto(
//                request.conversationId(),
//                user.getId(),
//                username,
//                started
//        );
//
//        Conversation conversation = conversationService.findById(request.conversationId());
//
//        if (conversation.isGroup()) {
//            String destination = "/topic/conversation." + request.conversationId() + ".typing";
//            System.out.println("Sending GROUP typing event to: " + destination);
//            messagingTemplate.convertAndSend(destination, event);
//        } else {
//            for (ConversationParticipant participant : conversation.getParticipants()) {
//                String recipientUsername = participant.getUser().getUsername();
//                if (!recipientUsername.equals(username)) {
//                    messagingTemplate.convertAndSendToUser(
//                            recipientUsername,
//                            "/queue/typing",
//                            event
//                    );
//                }
//            }
//        }
//    }
//}
