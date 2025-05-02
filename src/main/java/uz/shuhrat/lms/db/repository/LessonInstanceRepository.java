package uz.shuhrat.lms.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.LessonInstance;

import java.time.LocalDate;
import java.util.Optional;

public interface LessonInstanceRepository extends JpaRepository<LessonInstance, Long> {

    @Query("SELECT li FROM LessonInstance li WHERE li.lessonSchedule.id = :scheduleId AND li.lessonDate = :date")
    LessonInstance findByLessonScheduleIdAndDate(@Param("scheduleId") Long scheduleId, @Param("date") LocalDate date);

    @Query("SELECT MAX(li.lessonNumber) FROM LessonInstance li WHERE li.lessonSchedule.group.id = :groupId")
    Integer findMaxLessonNumberByGroupId(@Param("groupId") Long groupId);

    @Query("DELETE FROM LessonInstance li WHERE li.lessonSchedule.id = :scheduleId AND li.lessonDate > :date")
    void deleteByLessonScheduleIdAndLessonDateAfter(@Param("scheduleId") Long scheduleId, @Param("date") LocalDate date);

    @Query("SELECT li FROM LessonInstance li WHERE li.lessonSchedule.group = :group ORDER BY li.lessonDate ASC")
    Optional<LessonInstance> findTopByLessonScheduleGroupOrderByLessonDateAsc(@Param("group") Group group);
}