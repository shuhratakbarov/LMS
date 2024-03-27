package uz.farobiy.lesson_11_backend.rest;

import  org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.farobiy.lesson_11_backend.dto.form.LoginForm;
import uz.farobiy.lesson_11_backend.service.file.FileService;
import uz.farobiy.lesson_11_backend.service.admin.UserService;

@RestController
@RequestMapping("/auth")
public class AuthRestController {
   @Autowired
    private UserService userService;

   @PostMapping("/signin")
   public ResponseEntity signin(@RequestBody LoginForm form) throws Exception{
       return ResponseEntity.ok(userService.signin(form));
   }


//    @PostMapping("/test")
//    public ResponseEntity test(@RequestParam MultipartFile file){
//      return ResponseEntity.ok(fileService.save(file));
//    }
}
