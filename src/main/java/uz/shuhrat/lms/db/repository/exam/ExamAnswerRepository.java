package uz.shuhrat.lms.db.repository.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.ExamAnswer;

import java.util.List;

@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {

    List<ExamAnswer> findByAttendanceId(Long attendanceId);

    @Query("SELECT ea FROM ExamAnswer ea JOIN FETCH ea.question WHERE ea.attendance.id = :attendanceId")
    List<ExamAnswer> findByAttendanceIdWithQuestion(@Param("attendanceId") Long attendanceId);

    void deleteByAttendanceId(Long attendanceId);

    boolean existsByAttendanceIdAndQuestionId(Long attendanceId, Long questionId);
}