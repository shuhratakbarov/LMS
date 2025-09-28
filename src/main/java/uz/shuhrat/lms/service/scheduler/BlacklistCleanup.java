package uz.shuhrat.lms.service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.shuhrat.lms.db.repository.admin.TokenBlacklistRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BlacklistCleanup {
    private final TokenBlacklistRepository blacklistRepository;

    // Runs every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        blacklistRepository.deleteByExpirationDateBefore(Instant.now());
    }
}