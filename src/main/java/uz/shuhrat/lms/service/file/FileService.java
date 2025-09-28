package uz.shuhrat.lms.service.file;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.GeneralResponseDto;

public interface FileService {
    GeneralResponseDto<?> save(MultipartFile file);

    ResponseEntity<Resource> downloadFile(String fileId, Long groupId);

    GeneralResponseDto<?> delete(String pkey, String fileName);
}
