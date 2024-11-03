package uz.farobiy.lms_clone.service.file.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import uz.farobiy.lms_clone.db.domain.File;
import uz.farobiy.lms_clone.db.domain.Group;
import uz.farobiy.lms_clone.db.domain.User;
import uz.farobiy.lms_clone.db.repository.admin.GroupRepository;
import uz.farobiy.lms_clone.db.repository.file.FileRepository;
import uz.farobiy.lms_clone.dto.ResponseDto;
import uz.farobiy.lms_clone.helper.SecurityHelper;
import uz.farobiy.lms_clone.service.file.FileService;


import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private final String filePath = "C:\\lms-test\\files";
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public ResponseDto save(MultipartFile file) throws Exception {
        try {
            Path root = Paths.get(this.filePath);
            File f = new File();
            f.setPkey(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
            f.setSize(file.getSize());
            f.setName(file.getOriginalFilename());
            f.setPathUrl(this.filePath + "\\" + f.getPkey() + "-" + f.getName());

            String newFileName = f.getPkey() + "-" + f.getName();
            Path destination = root.resolve(newFileName);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);


            f = fileRepository.save(f);
            if (f.getPkey() != null) {
                return new ResponseDto<>(true, "ok", f);
            }
            return new ResponseDto<>(false, "error while saving file");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto delete(String pkey, String fileName) throws Exception {
        try {
//            Optional<Files> optionalFiles = fileRepository.findByPkey(pkey);
//            if (optionalFiles.isPresent()) {
//                Files file = optionalFiles.get();
//                Path filePath = Paths.get(file.getPathUrl());
//                java.nio.file.Files.deleteIfExists(filePath);
//                fileRepository.delete(file);
//                return new ResponseDto(true, "Fayl muvaffaqiyatli o'chirildi");
//            } else {
//                return new ResponseDto(false, "Fayl topilmadi");
//            }

            String path = filePath + "\\" +pkey+"-"+ fileName;
            Path filePath = Paths.get(path);
            Files.deleteIfExists(filePath);
            return new ResponseDto(true, "Fayl muvaffaqiyatli o'chirildi");


        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseDto(false, "Fayl o'chirishda xatolik yuz berdi: " + e.getMessage());
        }
    }


    @Override
    public ResponseEntity<Resource> downloadFile(String fileId, Long groupId) throws Exception {
        try {
            User currentUser = SecurityHelper.getCurrentUser();
            if (currentUser == null) {
                throw new Exception("Autentifikatsiyadan o'tishingiz kerak");
            }
            if (Objects.equals(currentUser.getRole().getName(), "ROLE_TEACHER")) {
                Optional<Group> groupOptional = groupRepository.findById(groupId);
                if (groupOptional.isPresent() && groupOptional.get().getTeacher().getId().equals(currentUser.getId())) {
                   return returnFile(fileId);
                } else {
                    throw new Exception("Error");
                } 
            } else if (Objects.equals(currentUser.getRole().getName(), "ROLE_STUDENT")) {
                Optional<Group> groupOptional = groupRepository.findById(groupId);
                if (groupOptional.isPresent() && groupOptional.get().getStudents().contains(currentUser)) {
                   return returnFile(fileId);
                } else {
                    throw new Exception("Error");
                }
            } else if (Objects.equals(currentUser.getRole().getName(), "ROLE_ADMIN")) {
                   return returnFile(fileId);
            } else {
                throw new Exception("Sur!");
            }

        } catch (Exception e) {
            System.out.println("File Service : " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    private ResponseEntity<Resource> returnFile(String fileId) throws Exception {
        Optional<File> fileOptional = fileRepository.findByPkey(fileId);
        if (fileOptional.isPresent()) {
            Path filePath = Paths.get(fileOptional.get().getPathUrl());
            Resource resource = new UrlResource(filePath.toUri());
            String type=Files.probeContentType(filePath);
            if (type == null) {
                type = "text/plain";
            }
            MediaType mediaType = MediaType.parseMediaType(type);
            if (resource.exists() || resource.isReadable()) {

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFile().getName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            throw new Exception("Fayl topilmadi");
        }
    }

}
