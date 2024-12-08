package uz.shuhrat.lms.service.file.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.shuhrat.lms.db.domain.attachment.Attachment;
import uz.shuhrat.lms.db.domain.attachment.AttachmentContent;
import uz.shuhrat.lms.db.repository.file.AttachmentContentRepository;
import uz.shuhrat.lms.db.repository.file.AttachmentRepository;
import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.service.file.AttachmentService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository contentRepository;

    @Autowired
    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, AttachmentContentRepository contentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    public ResponseDto<?> findAll() {
        List<Attachment> list = attachmentRepository.findAll();
        return new ResponseDto<>(true, "ok", list);
    }

    @Transactional
    public ResponseEntity<ResponseDto<?>> uploadFile(MultipartFile file) {
        try {
            Attachment attachment = new Attachment();
            attachment.setName(file.getOriginalFilename());
            attachment.setSize(file.getSize());
            attachmentRepository.save(attachment);
            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setAttachment(attachment);
            attachmentContent.setContentType(file.getContentType());
            attachmentContent.setBytes(file.getBytes());
            contentRepository.save(attachmentContent);
            return ResponseEntity.ok().body(new ResponseDto<>(true, "Fayl muvaffaqiyatli yuklandi"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(false, "Server xatosi: " + e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<ByteArrayResource> downloadFile(UUID fileId) {
        try {
            Optional<Attachment> attachmentOptional = attachmentRepository.findById(fileId);
            if (attachmentOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Attachment attachment = attachmentOptional.get();
            Optional<AttachmentContent> contentOptional = contentRepository.findById(attachment.getContent().getPkey());
            if (contentOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            AttachmentContent content = contentOptional.get();
            ByteArrayResource resource = new ByteArrayResource(content.getBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", attachment.getName());
            headers.setContentType(MediaType.parseMediaType(content.getContentType()));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(content.getBytes().length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<ResponseDto<?>> deleteFile(UUID fileId) {
        try {
            Optional<Attachment> attachmentOptional = attachmentRepository.findById(fileId);
            if (attachmentOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Optional<AttachmentContent> contentOptional = contentRepository.findById(attachmentOptional.get().getContent().getPkey());
            Attachment attachment = attachmentOptional.get();
            if (contentOptional.isPresent()) {
                contentRepository.deleteById(contentOptional.get().getPkey());
                attachmentRepository.deleteById(attachment.getPkey());
                return ResponseEntity.ok().body(new ResponseDto<>(true, "Fayl muvaffaqiyatli o'chirildi"));

            }
            return ResponseEntity.ok().body(new ResponseDto<>(false, "Fayl o'chirilmadi"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(false, "Server xatosi: " + e.getMessage()));
        }
    }
}
