package uz.farobiy.lms_clone.service.file;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lms_clone.dto.ResponseDto;
import java.util.UUID;

public interface AttachmentService {
    public ResponseDto findAll();
    public ResponseEntity<ResponseDto> uploadFile(MultipartFile file) throws Exception ;
    public ResponseEntity<ByteArrayResource> downloadFile(UUID fileId) throws Exception;
    public ResponseEntity<ResponseDto> deleteFile(UUID fileId)throws Exception;

}
