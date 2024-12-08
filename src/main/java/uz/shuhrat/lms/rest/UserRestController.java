package uz.shuhrat.lms.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.service.admin.UserService;

@RestController
@RequestMapping("/user")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-user-info")
    public ResponseEntity<?> getUserInfo() throws Exception {
        return ResponseEntity.ok(userService.currentUserInfo());
    }
}
