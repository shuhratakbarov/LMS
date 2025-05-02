package uz.shuhrat.lms.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.db.domain.Attendance;
import uz.shuhrat.lms.db.domain.LessonInstance;
import uz.shuhrat.lms.db.domain.LessonSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    @Query("SELECT a FROM Attendance a WHERE a.lessonInstance.id = :lessonInstanceId AND a.student.id = :studentId")
    Attendance findByLessonInstanceIdAndStudentId(@Param("lessonInstanceId") Long lessonInstanceId, @Param("studentId") UUID studentId);

    @Query("SELECT a.lessonInstance FROM Attendance a WHERE a.lessonInstance.lessonSchedule = :schedule ORDER BY a.lessonInstance.lessonDate DESC")
    Optional<LessonInstance> findTopLessonInstanceByLessonScheduleOrderByLessonDateDesc(@Param("schedule") LessonSchedule schedule);

    @Query("SELECT a FROM Attendance a WHERE a.lessonInstance.id = :lessonInstanceId AND a.student.id IN :studentIds")
    List<Attendance> findByLessonInstanceIdAndStudentIds(@Param("lessonInstanceId") Long lessonInstanceId, @Param("studentIds") List<UUID> studentIds);
}