package uz.farobiy.lms_clone.service.file;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lms_clone.dto.ResponseDto;

public interface FileService {
    ResponseDto save(MultipartFile file) throws Exception;
    ResponseEntity<Resource> downloadFile(String fileId, Long groupId) throws Exception;
    ResponseDto delete(String pkey, String fileName) throws Exception;
}
