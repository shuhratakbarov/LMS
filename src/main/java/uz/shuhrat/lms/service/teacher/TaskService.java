package uz.shuhrat.lms.service.teacher;

import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.TaskRequestDto;

import java.util.UUID;

public interface TaskService {
    GeneralResponseDto<?> saveTask(MultipartFile multipartFile, TaskRequestDto form) throws Exception;

    GeneralResponseDto<?> editTask(UUID taskId, TaskRequestDto form, MultipartFile multipartFile) throws Exception;

    GeneralResponseDto<?> deleteTask(UUID taskId) throws Exception;

    GeneralResponseDto<?> findAllWithFiles(Long groupId);
}
