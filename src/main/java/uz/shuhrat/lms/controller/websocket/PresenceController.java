package uz.shuhrat.lms.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import uz.shuhrat.lms.service.message.PresenceService;

import java.security.Principal;
import java.util.Set;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @MessageMapping("/online")
    @SendToUser("/queue/online")
    public Set<String> getOnlineUsers(Principal principal) {
        log.info("Manual online user fetch triggered for {}", principal.getName());
        return presenceService.getOnlineUsers();
    }
}
