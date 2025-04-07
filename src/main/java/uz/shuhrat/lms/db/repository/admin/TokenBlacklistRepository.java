package uz.shuhrat.lms.db.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.shuhrat.lms.db.domain.TokenBlacklist;

import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByToken(String token);
}