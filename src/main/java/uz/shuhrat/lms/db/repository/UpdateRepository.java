package uz.shuhrat.lms.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.db.domain.Update;

import java.util.List;

public interface UpdateRepository extends JpaRepository<Update, Long> {
    @Query(value = "SELECT * FROM updates u WHERE jsonb_exists(u.roles, :role)", nativeQuery = true)
    List<Update> findByRole(@Param("role") String role);



}
