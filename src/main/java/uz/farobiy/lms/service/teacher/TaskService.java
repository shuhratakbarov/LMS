package uz.farobiy.lms.service.teacher;

import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lms.dto.ResponseDto;
import uz.farobiy.lms.dto.form.CreateTaskForm;

import java.util.UUID;

public interface TaskService {
    ResponseDto<?> saveTask(MultipartFile multipartFile, CreateTaskForm form) throws Exception;

    ResponseDto<?> editTask(UUID taskId, CreateTaskForm form, MultipartFile multipartFile) throws Exception;

    ResponseDto<?> deleteTask(UUID taskId) throws Exception;

    ResponseDto<?> findAllWithFiles(Long groupId) throws Exception;
}
