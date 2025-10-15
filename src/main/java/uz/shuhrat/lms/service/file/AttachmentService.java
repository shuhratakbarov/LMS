package uz.shuhrat.lms.service.file;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.dto.GeneralResponseDto;

import java.util.UUID;

public interface AttachmentService {
    GeneralResponseDto<?> findAll();

    ResponseEntity<GeneralResponseDto<?>> uploadFile(MultipartFile file) throws Exception;

    ResponseEntity<ByteArrayResource> downloadFile(UUID fileId) throws Exception;

    ResponseEntity<GeneralResponseDto<?>> deleteFile(UUID fileId) throws Exception;

}
