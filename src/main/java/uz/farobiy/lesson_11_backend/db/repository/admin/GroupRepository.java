package uz.farobiy.lesson_11_backend.db.repository.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.farobiy.lesson_11_backend.db.domain.Group;
import uz.farobiy.lesson_11_backend.db.domain.customDto.admin.GroupCustomDto;
import uz.farobiy.lesson_11_backend.db.domain.customDto.admin.GroupIdAndName;
import uz.farobiy.lesson_11_backend.db.domain.customDto.admin.ViewGroupAndCount;
import uz.farobiy.lesson_11_backend.db.domain.customDto.admin.ViewGroupAndTeacher;


import java.util.*;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Page<Group> findAllByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT c FROM Group c WHERE LOWER(CONCAT(c.id, c.name,c.description, c.course.name)) LIKE LOWER(CONCAT('%', :searching, '%'))")
    Page<Group> search(@Param("searching") String searching, Pageable pageable);

    @Query(value = "SELECT g FROM groups g " +
            "LEFT JOIN group_student gu ON g.id = gu.group_id " +
            "WHERE g.teacher_id = :id OR gu.student_id = :id",
            nativeQuery = true)
    Page<Group> findAllByUserId(@Param("id") UUID id, Pageable pageable);

    @Modifying
    @Query(value = "insert into group_student(student_id,group_id) values(:studentId,:groupId)", nativeQuery = true)
    void addStudentToGroup(@Param("studentId") UUID studentId, @Param("groupId") Long groupId);

    @Query(value = "SELECT g.name, (SELECT COUNT(*) FROM group_student gs WHERE gs.group_id = g.id) AS count FROM groups g" +
            " WHERE g.teacher_id = :id", nativeQuery = true)
    List<ViewGroupAndCount> getGroupsAndCountByTeacherId(@Param("id") UUID id);

    @Query(value = "SELECT g.name AS groupName, CONCAT(u.first_name, ' ', u.last_name) AS teacherName FROM groups g JOIN users u ON g.teacher_id = u.id " +
            "WHERE g.id IN (SELECT group_id FROM group_student WHERE student_id = :id);", nativeQuery = true)
    List<ViewGroupAndTeacher> getGroupsAndTeacherByStudentId(@Param("id") UUID id);

//    @Query(nativeQuery = true,
//            value = "select  g.name, c.name as courseName,s.s_count as studentCount " +
//                    "from groups g  " +
//                    "left join courses c on c.id = g.course_id  " +
//                    "left join (  " +
//                    "select count(gs.group_id) as s_count, gs.group_id from group_student gs  " +
//                    "  group by gs.group_id  " +
//                    ") s on s.group_id = g.id;"
//    )
//    List<GroupCustomDto> getGroupsList();

    @Modifying
    @Query(value = "delete from group_student where student_id=:studentId and group_id=:groupId", nativeQuery = true)
    @Transactional
    void removeStudentFromGroupByStudentId(@Param("studentId") UUID studentId, @Param("groupId") Long groupId);

    @Query(value = "select id, name from groups ", nativeQuery = true)
    List<GroupIdAndName> getGroupIdAndName();

    @Query(value = "SELECT count(*) FROM groups WHERE id IN (:ids)", nativeQuery = true)
    Optional<Long> getCountByIds(List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO group_student (group_id, student_id) SELECT g.id, :studentId FROM groups g WHERE g.id IN :groupIds", nativeQuery = true)
    void addStudentToGroups(List<Long> groupIds, UUID studentId);
}

