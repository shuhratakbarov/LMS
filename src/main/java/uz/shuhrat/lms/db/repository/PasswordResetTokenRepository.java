package uz.shuhrat.lms.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.shuhrat.lms.db.domain.PasswordResetToken;
import uz.shuhrat.lms.db.domain.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser(User user);
    Optional<PasswordResetToken> findByToken(String token);

}
