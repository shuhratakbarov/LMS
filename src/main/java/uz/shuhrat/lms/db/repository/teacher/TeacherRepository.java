package uz.shuhrat.lms.db.repository.teacher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.customDto.teacher.GroupCustomForTeacher;
import uz.shuhrat.lms.db.customDto.teacher.HomeworkListDto;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Group, Long> {
    @Query(nativeQuery = true,
            value = """
                    select u.id         as studentId,
                           first_name   as firstName,
                           last_name    as lastName,
                           phone,
                           email,
                           h.id         as homeworkId,
                           ball,
                           description,
                           h.created_at as homeworkCreatedAt,
                           h.updated_at as homeworkUpdatedAt,
                           file_id      as homeworkFileId,
                           name         as homeworkFileName,
                           size         as homeworkFileSize
                    from users u
                             left join (select h.id,
                                               student_id,
                                               task_id,
                                               ball,
                                               description,
                                               h.created_at,
                                               h.updated_at,
                                               pkey as file_id,
                                               name,
                                               size
                                        from homework h
                                                 inner join files f on h.file_id = f.pkey
                                        where h.task_id = :taskId) h on h.student_id = u.id
                    where u.id in
                          (select student_id from group_student gs where gs.group_id = (select group_id from tasks where id = :taskId))
                      and u.active
                    """)
    Page<HomeworkListDto> getHomeworkByTaskId(@Param("taskId") UUID taskId, Pageable pageable);

    @Query(nativeQuery = true, value = """
                    SELECT g.id, g.name AS groupName,
                           c.id as courseId, c.name AS courseName,
                                    COUNT(DISTINCT gs.student_id) as studentCount,
                                    COUNT(DISTINCT t.id) as taskCount
                                    FROM groups g
                                             LEFT JOIN courses c ON c.id = g.course_id
                                             LEFT JOIN group_student gs ON gs.group_id = g.id
                                             LEFT JOIN tasks t ON t.group_id=g.id
                                    WHERE g.teacher_id = :teacherId
                                    AND LOWER(CONCAT(g.id, g.name, c.name)) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                    GROUP BY g.id, c.name, c.id
                    """)
    Page<GroupCustomForTeacher> getGroups(@Param("teacherId") UUID teacherId, @Param("keyword") String keyword, Pageable pageable);
}
