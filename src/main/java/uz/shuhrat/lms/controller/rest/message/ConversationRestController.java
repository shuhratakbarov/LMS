package uz.shuhrat.lms.controller.rest.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.dto.request.CreateConversationRequestDto;
import uz.shuhrat.lms.dto.response.*;
import uz.shuhrat.lms.service.message.ConversationService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationRestController {

    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<List<ConversationResponseDto>> getUserConversations(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        List<ConversationResponseDto> conversations = conversationService.getUserConversations(principal.getId());
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{conversationId}/message")
    public ResponseEntity<MessagePageDto> getConversationMessages(
            @PathVariable UUID conversationId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Principal principal
    ) {
        MessagePageDto response = conversationService.getConversationMessages(conversationId, pageable, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ConversationResponseDto> createConversation(
            Authentication authentication,
            @RequestBody CreateConversationRequestDto request
    ) {
        User principal = (User) authentication.getPrincipal();
        ConversationResponseDto conversation = conversationService.createConversation(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    @PostMapping("/{conversationId}/join")
    public ResponseEntity<Page<MessageSummaryDto>> joinGroup(
            @PathVariable UUID conversationId,
            Authentication authentication) throws Exception {
        User principal = (User) authentication.getPrincipal();
        return ResponseEntity.ok(conversationService.joinAndFetchMessages(conversationId, principal.getId()));
    }

    @GetMapping("/{conversationId}/read-receipts")
    public ResponseEntity<List<ReadReceiptDto>> getReadReceipts(
            @PathVariable UUID conversationId
    ) {
        List<ReadReceiptDto> receipts = conversationService.getReadReceipts(conversationId);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ConversationSearchResultDto>> searchConversations(
            @RequestParam String term,
            @RequestParam String type, // "user" or "group"
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(conversationService.searchConversations(term, username, type));
    }
}
