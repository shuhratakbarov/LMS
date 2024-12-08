package uz.shuhrat.lms.service.student;

import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.ResponseDto;

import java.util.UUID;

public interface HomeworkService {
    ResponseDto<?> saveHomework(UUID taskId, UUID homeworkId, MultipartFile file) throws Exception;
}
