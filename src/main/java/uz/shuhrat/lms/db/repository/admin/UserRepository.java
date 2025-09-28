package uz.shuhrat.lms.db.repository.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.projection.UserSummaryProjection;
import uz.shuhrat.lms.enums.Role;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.username  FROM users u JOIN group_student ug ON u.id = ug.student_id WHERE ug.group_id = :id and u.active", nativeQuery = true)
    Page<UserSummaryProjection> getStudentsOfGroup(@Param("id") Long id, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM group_student WHERE group_id = :id ", nativeQuery = true)
    Optional<Long> countStudentsByGroupId(@Param("id") Long id);

    @Query(value = "select u.id, u.username from users u where u.role = 'TEACHER' and u.active", nativeQuery = true)
    List<Map<String, Object>> findTeachersForSelect();

    @Query("SELECT u FROM User u WHERE u.role=:role and LOWER(CONCAT(u.id, u.firstName, u.lastName, u.username, u.phone)) LIKE LOWER(CONCAT('%', :searching, '%')) ")
    Page<User> getUserList(@Param("searching") String searching, @Param("role") Role role, Pageable pageable);

    @Query("""
    SELECT u FROM User u
    WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<User> findByUsernameOrNameContaining(@Param("query") String query);
}
