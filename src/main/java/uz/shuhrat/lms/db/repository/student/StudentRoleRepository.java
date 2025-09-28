package uz.shuhrat.lms.db.repository.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.projection.CourseGroupCountProjection;
import uz.shuhrat.lms.projection.GroupsOfStudentProjection;
import uz.shuhrat.lms.projection.HomeworkNotificationDetailsProjection;
import uz.shuhrat.lms.projection.StudentHomeworkProjection;
import uz.shuhrat.lms.db.domain.Homework;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRoleRepository extends JpaRepository<Homework, UUID> {
    @Query(nativeQuery = true,
            value = """
                    SELECT g.id as id, g.name AS groupName, CONCAT(u.first_name, ' ', u.last_name) AS teacherName, c.name AS courseName
                    FROM group_student gs
                    JOIN groups g ON gs.group_id = g.id
                    JOIN users u ON g.teacher_id = u.id
                    JOIN courses c ON g.course_id = c.id
                    WHERE gs.student_id = :studentId
                    AND LOWER(CONCAT(g.id, g.name, u.first_name, u.last_name, c.name)) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    GROUP BY g.id, c.name, c.id, u.first_name, u.last_name
                    """
    )
    Page<GroupsOfStudentProjection> getGroupsOfStudent(@Param("studentId") UUID studentId, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select * from " +
                   " (select t.id as taskId, t.deadline, t.type, t.max_ball as maxBall, t.name as taskName, t.file_id as taskFileId, " +
                   " f.name as taskFileName, f.path_url as taskPathUrl, f.size as taskFileSize from tasks t " +
                   " left join files f on f.pkey=t.file_id where group_id=:groupId) t " +
                   " left join (select h.id as homeworkId, h.ball as homeworkBall, h.file_id as homeworkFileId, h.task_id, h.description, " +
                   " f.name as homeworkFileName, f.path_url as homeworkPathUrl, f.size as homeworkFileSize from homework h " +
                   " left join files f on f.pkey=h.file_id where h.student_id=:studentId) h on t.taskId=h.task_id order by t.deadline ",
            countQuery = "select count(1) from " +
                         " (select t.id from tasks t " +
                         " left join files f on f.pkey = t.file_id where group_id = :groupId) t " +
                         " left join (select h.id, h.task_id from homework h " +
                         " left join files f on f.pkey = h.file_id where h.student_id = :studentId) h on t.id = h.task_id",
            nativeQuery = true)
    Page<StudentHomeworkProjection> getHomeworksOfStudent(@Param("groupId") Long groupId, @Param("studentId") UUID studentId, Pageable pageable);

    @Query(nativeQuery = true, value =
            "SELECT " +
            "c.name AS courseName, " +
            "g.name AS groupName, " +
            "g.id AS groupId, " +
            "t.name AS taskName, " +
            "t.type AS taskType, " +
            "t.deadline, " +
            "EXTRACT(DAY FROM (t.deadline - CURRENT_DATE)) AS daysLeft, " +
            "t.id AS taskId " +
            "FROM ( " +
            "SELECT * FROM tasks " +
            "WHERE group_id IN ( " +
            "SELECT DISTINCT group_id " +
            "FROM group_student " +
            "WHERE student_id = :studentId " +
            ") " +
            ") t " +
            "LEFT JOIN ( " +
            "SELECT * FROM homework " +
            "WHERE student_id = :studentId " +
            ") h ON h.task_id = t.id " +
            "INNER JOIN groups g ON t.group_id = g.id " +
            "INNER JOIN courses c ON g.course_id = c.id " +
            "WHERE h.id IS NULL " +
            "AND t.deadline > CURRENT_TIMESTAMP " +
            "ORDER BY " +
            "t.deadline ASC, " +
            "c.name, " +
            "t.name " +
            "LIMIT 15")
    List<HomeworkNotificationDetailsProjection> getHomeworkNotificationDetails(@Param("studentId") UUID studentId);
    @Query(nativeQuery = true,
            value = """
                    SELECT COUNT(DISTINCT g.course_id) AS "courseCount",
                        COUNT(DISTINCT gs.group_id) AS "groupCount"
                    FROM group_student gs
                        JOIN groups g ON gs.group_id = g.id
                    WHERE gs.student_id=:studentId
                    GROUP BY gs.student_id;
                    """)
    CourseGroupCountProjection getStudentCourseAndGroupCount(@Param("studentId") UUID studentId);

//    @Query(nativeQuery = true,
//            value = "select count(*) from (select * from tasks " +
//                    " where group_id in (select distinct group_id from group_student where student_id=:studentId)) t " +
//                    " left join (select * from homework where student_id=:studentId) h on h.task_id=t.id " +
//                    " where h.id IS NULL " +
//                    " AND t.deadline > CURRENT_TIMESTAMP")
//    List<Integer> getHomeworkCount(@Param("studentId") UUID studentId);
}
