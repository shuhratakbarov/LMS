package uz.shuhrat.lms.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.LessonSchedule;

import java.util.List;

public interface LessonScheduleRepository extends JpaRepository<LessonSchedule, Long> {
    @Query("SELECT ls FROM LessonSchedule ls WHERE ls.room.id = :roomId")
    List<LessonSchedule> findByRoomId(@Param("roomId") Long roomId);

    @Query(value = "SELECT ls FROM LessonSchedule ls WHERE LOWER(CONCAT(ls.id, ls.group.name,ls.day, ls.startTime, ls.endTime)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<LessonSchedule> getLessonSchedule(@Param("keyword") String keyword, Pageable pageable);
}