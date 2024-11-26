package uz.farobiy.lms.service.file;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lms.dto.ResponseDto;

import java.util.UUID;

public interface AttachmentService {
    ResponseDto<?> findAll();

    ResponseEntity<ResponseDto<?>> uploadFile(MultipartFile file) throws Exception;

    ResponseEntity<ByteArrayResource> downloadFile(UUID fileId) throws Exception;

    ResponseEntity<ResponseDto<?>> deleteFile(UUID fileId) throws Exception;

}
