package uz.farobiy.lesson_11_backend.service.student;

import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lesson_11_backend.dto.ResponseDto;

import java.util.UUID;

public interface HomeworkService {
    ResponseDto saveHomework(UUID taskId, UUID homeworkId, MultipartFile file) throws Exception;
}
