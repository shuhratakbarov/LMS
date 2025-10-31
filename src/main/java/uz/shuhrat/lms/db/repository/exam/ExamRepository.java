package uz.shuhrat.lms.db.repository.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.Exam;
import uz.shuhrat.lms.enums.ExamStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByTeacherId(UUID teacherId);

    List<Exam> findByCourseIdInAndStatus(List<Long> courseIds, ExamStatus status);

    List<Exam> findByStatus(ExamStatus status);

    @Query("SELECT e FROM Exam e WHERE e.course.id = :courseId AND e.status = :status AND e.deadline > :now")
    List<Exam> findUpcomingExamsByCourseAndStatus(
            @Param("courseId") Long courseId,
            @Param("status") ExamStatus status,
            @Param("now") Instant now
    );

    @Query("SELECT e FROM Exam e WHERE e.teacher.id = :teacherId AND e.status = :status")
    List<Exam> findByTeacherIdAndStatus(
            @Param("teacherId") UUID teacherId,
            @Param("status") ExamStatus status
    );

    boolean existsByIdAndTeacherId(Long id, UUID teacherId);
}
