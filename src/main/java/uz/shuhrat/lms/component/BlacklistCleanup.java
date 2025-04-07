package uz.shuhrat.lms.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.shuhrat.lms.db.repository.admin.TokenBlacklistRepository;

import java.time.Instant;

@Component
public class BlacklistCleanup {
    private final TokenBlacklistRepository blacklistRepository;

    @Autowired
    public BlacklistCleanup(TokenBlacklistRepository blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void cleanupExpiredTokens() {
        blacklistRepository.deleteAll(
                blacklistRepository.findAll().stream()
                        .filter(t -> t.getExpirationDate().isBefore(Instant.now()))
                        .toList());
    }
}