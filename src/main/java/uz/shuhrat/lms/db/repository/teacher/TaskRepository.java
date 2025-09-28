package uz.shuhrat.lms.db.repository.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.projection.TaskListWithGroupNameProjection;
import uz.shuhrat.lms.db.domain.Task;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query(nativeQuery = true,
            value = """
                    SELECT
                        t.id,
                        t.deadline,
                        t.max_ball AS maxBall,
                        t.name AS taskName,
                        t.created_at AS createdAt,
                        t.updated_at AS updatedAt,
                        t.type,
                        f.pkey,
                        f.name AS fileName,
                        f.size,
                        g.name AS groupName
                    FROM
                        tasks t
                            LEFT JOIN
                        files f ON f.pkey = t.file_id
                            INNER JOIN
                        groups g ON g.id = t.group_id
                    WHERE
                        t.group_id = :groupId
                    ORDER BY t.deadline
                    """
    )
    List<TaskListWithGroupNameProjection> getGroupTasks(@Param("groupId") Long groupId);
}
