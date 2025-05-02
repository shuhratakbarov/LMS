package uz.shuhrat.lms.db.repository.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.customDto.admin.GroupIdAndName;
import uz.shuhrat.lms.db.customDto.admin.ViewGroupAndCount;
import uz.shuhrat.lms.db.customDto.admin.ViewGroupAndTeacher;
import uz.shuhrat.lms.db.domain.LessonSchedule;


import java.time.LocalDate;
import java.util.*;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT ls FROM LessonSchedule ls WHERE ls.room.id = :roomId")
    List<LessonSchedule> findLessonSchedulesByRoomId(@Param("roomId") Long roomId);

    Page<Group> findAllByCourseId(Long courseId, Pageable pageable);

    @Query(value = "SELECT g FROM Group g WHERE LOWER(CONCAT(g.id, g.name,g.description, g.course.name)) LIKE LOWER(CONCAT('%', :searching, '%'))")
    Page<Group> getGroups(@Param("searching") String searching, Pageable pageable);

    @Query(value = "SELECT g.name, (SELECT COUNT(*) FROM group_student gs WHERE gs.group_id = g.id) AS count FROM groups g" +
                   " WHERE g.teacher_id = :id", nativeQuery = true)
    List<ViewGroupAndCount> getGroupsAndCountByTeacherId(@Param("id") UUID id);

    @Query(value = "SELECT g.name AS groupName, CONCAT(u.first_name, ' ', u.last_name) AS teacherName FROM groups g JOIN users u ON g.teacher_id = u.id " +
                   "WHERE g.id IN (SELECT group_id FROM group_student WHERE student_id = :id);", nativeQuery = true)
    List<ViewGroupAndTeacher> getGroupsAndTeacherByStudentId(@Param("id") UUID id);

    @Modifying
    @Query(value = "delete from group_student where student_id=:studentId and group_id=:groupId", nativeQuery = true)
    @Transactional
    void removeStudentFromGroupByStudentId(@Param("studentId") UUID studentId, @Param("groupId") Long groupId);

    @Query(value = "select id, name from groups order by name", nativeQuery = true)
    List<GroupIdAndName> getGroupIdAndName();

    @Query(value = "SELECT count(*) FROM groups WHERE id IN (:ids)", nativeQuery = true)
    Optional<Long> getCountByIds(List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO group_student (group_id, student_id) SELECT g.id, :studentId FROM groups g WHERE g.id IN :groupIds", nativeQuery = true)
    void addStudentToGroups(List<Long> groupIds, UUID studentId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE groups SET teacher_id=:teacherId where id IN (:groupIds)", nativeQuery = true)
    void associateTeacherToGroups(List<Long> groupIds, @Param("teacherId") UUID teacherId);
}
