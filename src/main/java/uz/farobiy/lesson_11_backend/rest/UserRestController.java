package uz.farobiy.lesson_11_backend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.farobiy.lesson_11_backend.service.admin.UserService;

@RestController
@RequestMapping("/user")
public class UserRestController {

    @Autowired
    private UserService userService;


    @GetMapping("/get-user-info")
    public ResponseEntity getUserInfo() throws Exception {
        return ResponseEntity.ok(userService.currentUserInfo());
    }


}
