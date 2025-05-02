package uz.shuhrat.lms.db.repository.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.Course;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE LOWER(CONCAT(c.id, c.name, c.description)) LIKE LOWER(CONCAT('%', :searching, '%'))")
    Page<Course> getCourseList(@Param("searching") String searching, Pageable pageable);

    @Query("SELECT COUNT(g) FROM Group g JOIN g.course c WHERE c.id = :id")
    Optional<Long> countGroupsByCourseId(@Param("id") Long id);

    @Query(value = "SELECT new map(c.id as id, c.name as name) FROM Course c")
    List<Map<String, Object>> findCoursesForSelect();
}
