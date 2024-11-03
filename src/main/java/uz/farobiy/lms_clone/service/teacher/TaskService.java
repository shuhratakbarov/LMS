package uz.farobiy.lms_clone.service.teacher;
import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lms_clone.dto.ResponseDto;
import uz.farobiy.lms_clone.dto.form.CreateTaskForm;
import java.util.UUID;


public interface TaskService {

    ResponseDto saveTask(MultipartFile multipartFile, CreateTaskForm form) throws Exception;
    ResponseDto editTask(UUID taskId, CreateTaskForm form, MultipartFile multipartFile) throws Exception;
    ResponseDto deleteTask(UUID taskId) throws Exception;
    ResponseDto findAllWithFiles(Long groupId) throws Exception;
}
