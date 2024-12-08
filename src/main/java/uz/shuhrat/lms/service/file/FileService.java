package uz.shuhrat.lms.service.file;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.ResponseDto;

public interface FileService {
    ResponseDto<?> save(MultipartFile file);

    ResponseEntity<Resource> downloadFile(String fileId, Long groupId);

    ResponseDto<?> delete(String pkey, String fileName);
}
