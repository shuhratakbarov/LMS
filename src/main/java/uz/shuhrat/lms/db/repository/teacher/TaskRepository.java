package uz.shuhrat.lms.db.repository.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.shuhrat.lms.db.customDto.teacher.TaskList;
import uz.shuhrat.lms.db.domain.Task;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query(value = " select t.id, t.name  from tasks t  " +
                   " where group_id = :groupId", nativeQuery = true)
    List<TaskList> getTasks(@Param("groupId") Long groupId);
}
