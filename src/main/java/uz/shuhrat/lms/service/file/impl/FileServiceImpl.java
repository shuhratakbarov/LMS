package uz.shuhrat.lms.service.file.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import uz.shuhrat.lms.db.domain.File;
import uz.shuhrat.lms.db.domain.Group;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.db.repository.admin.GroupRepository;
import uz.shuhrat.lms.db.repository.file.FileRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.file.FileService;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Value("${app.file.storage.path}")
    private String filePath;
    private final FileRepository fileRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, GroupRepository groupRepository) {
        this.fileRepository = fileRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public GeneralResponseDto<?> save(MultipartFile file) {
        Path root = Paths.get(this.filePath);
        File f = new File();
        f.setPkey(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        f.setSize(file.getSize());
        f.setName(file.getOriginalFilename());
        String newFileName = f.getPkey() + "-" + f.getName();
        Path destination = root.resolve(newFileName);
        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            f = fileRepository.save(f);
        } catch (Exception e) {
            System.out.println("File Service save method: " + e.getMessage());
            return new GeneralResponseDto<>(false, e.getMessage());
        }
        if (f.getPkey() != null) {
            return new GeneralResponseDto<>(true, "ok", f);
        }
        return new GeneralResponseDto<>(false, "error while saving file");

    }

    @Override
    public GeneralResponseDto<?> delete(String pkey, String fileName) {
        String path = filePath + "\\" + pkey + "-" + fileName;
        Path filePath = Paths.get(path);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.out.println("File Service delete method: " + e.getMessage());
            return new GeneralResponseDto<>(false, "Fayl o'chirishda xatolik yuz berdi: " + e.getMessage());
        }
        return new GeneralResponseDto<>(true, "Fayl muvaffaqiyatli o'chirildi");
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String fileId, Long groupId) {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                throw new Exception("Autentifikatsiyadan o'tishingiz kerak");
            }
            if (currentUser.getRole() == Role.TEACHER) {
                Optional<Group> groupOptional = groupRepository.findById(groupId);
                if (groupOptional.isPresent() && groupOptional.get().getTeacher().getId().equals(currentUser.getId())) {
                    return returnFile(fileId);
                } else {
                    throw new Exception("Error");
                }
            } else if (currentUser.getRole() == Role.STUDENT) {
                Optional<Group> groupOptional = groupRepository.findById(groupId);
                if (groupOptional.isPresent()) {
                    List<User> groupStudents = groupOptional.get().getStudents();
                    List<UUID> grStIds = groupStudents.stream()
                            .map(User::getId)
                            .toList();
                    if (grStIds.stream().anyMatch(id -> id.equals(currentUser.getId()))) {
                        return returnFile(fileId);
                    } else {
                        throw new Exception("student bu guruhda yo'q");
                    }
                } else {
                    throw new Exception("Error");
                }
            } else if (currentUser.getRole() == Role.ADMIN) {
                return returnFile(fileId);
            } else {
                throw new Exception("Autentifikatsiyadn o'ting!");
            }
        } catch (Exception e) {
            System.out.println("File Service download method: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<Resource> returnFile(String fileId) {
        Optional<File> fileOptional = fileRepository.findByPkey(fileId);
        if (fileOptional.isPresent()) {
            Path filePath = Paths.get(this.filePath + "\\" + fileOptional.get().getPkey() + "-" + fileOptional.get().getName());
            Resource resource;
            String type;
            try {
                resource = new UrlResource(filePath.toUri());
                type = Files.probeContentType(filePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (type == null) {
                type = "text/plain";
            }
            MediaType mediaType = MediaType.parseMediaType(type);
            if (resource.exists() || resource.isReadable()) {
                try {
                    return ResponseEntity.ok()
                            .contentType(mediaType)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFile().getName() + "\"")
                            .body(resource);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return ResponseEntity.noContent().build();
            }
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
