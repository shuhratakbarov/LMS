package uz.shuhrat.lms.db.repository.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.db.customDto.admin.UserCustomDtoForAdmin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.username  FROM users u JOIN group_student ug ON u.id = ug.student_id WHERE ug.group_id = :id and u.active", nativeQuery = true)
    Page<UserCustomDtoForAdmin> getStudentsOfGroup(@Param("id") Long id, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM group_student WHERE group_id = :id ", nativeQuery = true)
    Optional<Long> countStudentsByGroupId(@Param("id") Long id);

    @Query(value = "select * from users u where u.role_id=:roleId", nativeQuery = true)
    Page<User> findAllByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    @Query(value = "select * from users u where u.role_id = :roleId and u.active IS TRUE ", nativeQuery = true)
    Page<User> findAllByRoleIdAndActive(@Param("roleId") Long roleId, Pageable pageable);

    @Query(value = "select * from users u where u.role_id = :roleId and u.active IS FALSE ", nativeQuery = true)
    Page<User> findAllByRoleIdAndNotActive(@Param("roleId") Long roleId, Pageable pageable);

    @Query(value = "select u.id, u.username from users u where u.role_id = 2 and u.active", nativeQuery = true)
    List<Map<String, Object>> findTeachersForSelect();

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.id, u.firstName, u.lastName, u.username, u.phone)) LIKE LOWER(CONCAT('%', :searching, '%')) and u.role.id=:roleId and u.active=:isActive ")
    Page<User> searchInRoleId(@Param("searching") String searching, @Param("roleId") Long roleId, @Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.id, u.firstName, u.lastName, u.username, u.phone)) LIKE LOWER(CONCAT('%', :searching, '%')) and u.role.id=:roleId ")
    Page<User> searchInRoleIdAll(@Param("searching") String searching, @Param("roleId") Long roleId, Pageable pageable);
}
