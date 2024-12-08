package uz.shuhrat.lms.db.repository.teacher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.customDto.teacher.GroupCustomForTeacher;
import uz.shuhrat.lms.db.customDto.teacher.StudentCustomDtoForTeacher;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Group, Long> {
    @Query(nativeQuery = true,
            value = """
                    SELECT g.id, g.name AS groupName, c.id as courseId, c.name AS courseName, COUNT(gs.student_id) as studentCount
                    FROM groups g
                    LEFT JOIN courses c ON c.id = g.course_id
                    LEFT JOIN group_student gs ON gs.group_id = g.id
                    WHERE g.teacher_id = :teacherId
                    GROUP BY g.id, c.name, c.id
                    """
    )
    Page<GroupCustomForTeacher> getGroup(@Param("teacherId") UUID teacherId, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT g.id, g.name AS groupName, c.id as courseId, c.name AS courseName, COUNT(gs.student_id) as studentCount " +
                                       " FROM groups g  " +
                                       " LEFT JOIN courses c ON c.id = g.course_id " +
                                       " LEFT JOIN group_student gs ON gs.group_id = g.id " +
                                       " WHERE g.teacher_id=:teacherId and g.course_id=:courseId " +
                                       " GROUP BY g.id, c.name, c.id"
    )
    Page<GroupCustomForTeacher> getGroupsByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") UUID teacherId, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT " +
                    "    t.id AS taskId, " +
                    "    t.deadline, " +
                    "    t.max_ball AS maxBall, " +
                    "    t.file_id AS taskFileId, " +
                    "    t.name AS taskName, " +
                    "    f.name AS taskFileName, " +
                    "    f.size AS taskFileSize, " +
                    "    f.path_url AS taskPathUrl, " +
                    "    sth.* " +
                    "FROM " +
                    "    (tasks t LEFT JOIN files f ON f.pkey = t.file_id), " +
                    "    ( " +
                    "        ( " +
                    "            SELECT " +
                    "                s.id AS studentId, " +
                    "                s.first_name AS firstName, " +
                    "                s.last_name AS lastName, " +
                    "                s.phone, " +
                    "                s.email " +
                    "            FROM " +
                    "                users s " +
                    "            WHERE " +
                    "                s.id IN ( " +
                    "                    SELECT " +
                    "                        gs.student_id " +
                    "                    FROM " +
                    "                        group_student gs " +
                    "                    WHERE " +
                    "                        group_id = :groupId " +
                    "                ) " +
                    "                AND s.active " +
                    "        ) st " +
                    "        LEFT JOIN ( " +
                    "            SELECT " +
                    "                h.id AS homeworkId, " +
                    "                h.ball AS homeworkBall, " +
                    "                h.student_id, " +
                    "                h.file_id AS homeworkFileId, " +
                    "                h.description, " +
                    "                h.task_id, " +
                    "                f.name AS homeworkName, " +
                    "                f.size AS homeworkSize, " +
                    "                f.path_url AS homeworkPathUrl " +
                    "            FROM " +
                    "                homework h " +
                    "                LEFT JOIN files f ON f.pkey = h.file_id " +
                    "            WHERE " +
                    "                h.task_id = :taskId " +
//                    "( " +
//                    "                    CASE " +
//                    "                        WHEN :taskId IS NULL THEN ( " +
//                    "                            SELECT " +
//                    "                                t.id " +
//                    "                            FROM " +
//                    "                                tasks t " +
//                    "                            WHERE " +
//                    "                                t.group_id = :groupId " +
//                    "                            ORDER BY " +
//                    "                                t.deadline ASC " +
//                    "                            LIMIT 1 " +
//                    "                        ) " +
//                    "                        ELSE " +
//                    "                            :taskId " +
//                    "                    END " +
//                    ") " +
                    "        ) h ON h.student_id = st.studentId " +
                    "    ) sth " +
                    "WHERE " +
                    "    t.id = ( " +
                    "        CASE " +
                    "            WHEN :taskId='5a864d1f-48a6-4157-bf8d-76ba1b777b5c' THEN ( " +
                    "                SELECT " +
                    "                    t.id " +
                    "                FROM " +
                    "                    tasks t " +
                    "                WHERE " +
                    "                    t.group_id = :groupId " +
                    "                ORDER BY " +
                    "                    t.deadline " +
                    "                LIMIT 1 " +
                    "            ) " +
                    "            ELSE " +
                    "                :taskId " +
                    "        END " +
                    "    ) "
    )
    Page<StudentCustomDtoForTeacher> getStudentOfGroup(@Param("groupId") Long groupId, @Param("taskId") UUID taskId, Pageable pageable);

    @Query(nativeQuery = true, value = "select id from groups where teacher_id=:teacherId")
    List<Long> getGroupsOfTeacher(@Param("teacherId") UUID teacherId);
}
