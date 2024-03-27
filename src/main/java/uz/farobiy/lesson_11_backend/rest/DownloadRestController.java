package uz.farobiy.lesson_11_backend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.farobiy.lesson_11_backend.service.file.FileService;

@RestController
@RequestMapping("/download")
public class DownloadRestController {
    @Autowired
    private FileService fileService;

    @GetMapping("/{groupId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("groupId") String groupId, @RequestParam("fileId") String fileId) throws Exception {
        Long groupID = Long.parseLong(groupId);
        return fileService.downloadFile(fileId, groupID);
    }
}

