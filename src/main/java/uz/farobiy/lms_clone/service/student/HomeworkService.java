package uz.farobiy.lms_clone.service.student;

import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lms_clone.dto.ResponseDto;

import java.util.UUID;

public interface HomeworkService {
    ResponseDto saveHomework(UUID taskId, UUID homeworkId, MultipartFile file) throws Exception;
}
