package uz.shuhrat.lms.service.student;

import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.GeneralResponseDto;

import java.util.UUID;

public interface HomeworkService {
    GeneralResponseDto<?> saveHomework(UUID taskId, UUID homeworkId, MultipartFile file) throws Exception;
}
