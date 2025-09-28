package uz.shuhrat.lms.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.service.file.FileService;

@RestController
@RequestMapping("/download")
public class DownloadRestController {
    private final FileService fileService;

    @Autowired
    public DownloadRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("groupId") String groupId,
                                                 @RequestParam("file-id") String fileId) {
        return fileService.downloadFile(fileId, Long.parseLong(groupId));
    }
}
