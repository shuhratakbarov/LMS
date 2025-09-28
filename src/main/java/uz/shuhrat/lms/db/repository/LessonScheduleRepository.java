package uz.shuhrat.lms.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.projection.LessonScheduleProjection;
import uz.shuhrat.lms.db.domain.LessonSchedule;

import java.util.List;
import java.util.UUID;

public interface LessonScheduleRepository extends JpaRepository<LessonSchedule, Long> {
    List<LessonSchedule> findByGroupId(Long groupId);
    @Query("SELECT ls FROM LessonSchedule ls WHERE ls.room.id = :roomId")
    List<LessonSchedule> findByRoomId(@Param("roomId") Long roomId);

    @Query(value = "SELECT ls FROM LessonSchedule ls WHERE LOWER(CONCAT(ls.id, ls.group.name,ls.day, ls.startTime, ls.endTime)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<LessonSchedule> getLessonSchedule(@Param("keyword") String keyword, Pageable pageable);

    //    @Query(value = "SELECT ls FROM LessonSchedule ls WHERE ls.group.id IN(SELECT g.id FROM Group g WHERE g.teacher.id = :teacherId)")
    @Query(value = """
                    select ls.day,
                           ls.start_time as startTime,
                           ls.end_time as endTime,
                           g.name as groupName,
                           r.name as roomName
                    from lesson_schedules ls
                           join groups g on g.id = ls.group_id
                           join rooms r on r.id = ls.room_id
                    where g.teacher_id = :teacherId
            """, nativeQuery = true)
    List<LessonScheduleProjection> getTeacherLessonSchedule(@Param("teacherId") UUID teacherId);

    @Query(value = """
                    select ls.day,
                           ls.start_time as startTime,
                           ls.end_time as endTime,
                           g.name as groupName,
                           r.name as roomName,
                           c.name as courseName
                    from lesson_schedules ls
                    join groups g on g.id = ls.group_id
                    join rooms r on r.id = ls.room_id
                    join courses c on c.id = g.course_id
                    where group_id in (select group_id from group_student where student_id = :studentId)
            """, nativeQuery = true)
    List<LessonScheduleProjection> getStudentLessonSchedule(@Param("studentId") UUID studentId);

    @Query(value = """
                    select * from lesson_schedules
                    where group_id in(select id from groups where teacher_id=:teacherId)
                    
""", nativeQuery = true)
    List<LessonSchedule> findByGroupTeacherId(UUID teacherId);
}