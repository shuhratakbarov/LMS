package uz.shuhrat.lms.db.repository.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.ExamAttendance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamAttendanceRepository extends JpaRepository<ExamAttendance, Long> {

    Optional<ExamAttendance> findByExamIdAndStudentId(Long examId, UUID studentId);

    boolean existsByExamIdAndStudentId(Long examId, UUID studentId);

    List<ExamAttendance> findByStudentId(UUID studentId);

    List<ExamAttendance> findByExamId(Long examId);

    @Query("SELECT ea FROM ExamAttendance ea WHERE ea.student.id = :studentId AND ea.submittedAt IS NOT NULL")
    List<ExamAttendance> findCompletedByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT ea FROM ExamAttendance ea WHERE ea.student.id = :studentId AND ea.submittedAt IS NULL")
    List<ExamAttendance> findInProgressByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT COUNT(ea) FROM ExamAttendance ea WHERE ea.exam.id = :examId AND ea.submittedAt IS NOT NULL")
    long countSubmittedByExamId(@Param("examId") Long examId);

    @Query("SELECT ea FROM ExamAttendance ea JOIN FETCH ea.exam e WHERE ea.student.id = :studentId AND ea.exam.course.id = :courseId")
    List<ExamAttendance> findByStudentIdAndCourseId(
            @Param("studentId") UUID studentId,
            @Param("courseId") Long courseId
    );
}
