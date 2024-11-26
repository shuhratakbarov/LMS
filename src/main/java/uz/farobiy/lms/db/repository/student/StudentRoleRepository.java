package uz.farobiy.lms.db.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.farobiy.lms.db.customDto.student.StudentDto;
import uz.farobiy.lms.db.customDto.student.StudentHomeworkDto;
import uz.farobiy.lms.db.domain.Homework;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRoleRepository extends JpaRepository<Homework, UUID> {
    @Query(value = "SELECT g.id as id, g.name AS groupName, CONCAT(u.first_name, ' ', u.last_name) AS teacherName, c.name AS courseName" +
                   " FROM group_student gs" +
                   " JOIN groups g ON gs.group_id = g.id" +
                   " JOIN users u ON g.teacher_id = u.id" +
                   " JOIN courses c ON g.course_id = c.id" +
                   " WHERE gs.student_id = :id", nativeQuery = true)
    List<StudentDto> getGroupsOfStudent(@Param("id") UUID id);

    @Query(value = "select * from " +
                   " (select t.id as taskId, t.deadline, t.max_ball as maxBall, t.name as taskName, t.file_id as taskFileId, " +
                   " f.name as taskFileName, f.path_url as taskPathUrl, f.size as taskFileSize from tasks t " +
                   " left join files f on f.pkey=t.file_id where group_id=:groupId) t " +
                   " left join (select h.id as homeworkId, h.ball as homeworkBall, h.file_id as homeworkFileId, h.task_id, h.description, " +
                   " f.name as homeworkFileName, f.path_url as homeworkPathUrl, f.size as homeworkFileSize from homework h " +
                   " left join files f on f.pkey=h.file_id where h.student_id=:studentId) h on t.taskId=h.task_id order by t.deadline", nativeQuery = true)
    List<StudentHomeworkDto> getHomeworksOfStudent(@Param("groupId") Long groupId, @Param("studentId") UUID studentId);

    @Query(nativeQuery = true,
            value = "select count(*) from (select * from tasks " +
                    " where group_id in (select distinct group_id from group_student where student_id=:studentId)) t " +
                    " left join (select * from homework where student_id=:studentId) h on h.task_id=t.id " +
                    " where h.id IS NULL " +
                    " AND t.deadline > CURRENT_TIMESTAMP")
    List<Integer> getHomeworkCount(@Param("studentId") UUID studentId);
}
