package uz.farobiy.lms.rest.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.farobiy.lms.service.admin.UserService;

@RestController
@RequestMapping("/student/group")
public class StudentGroupRestController {
    private final UserService userService;

    @Autowired
    public StudentGroupRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/students-of-group/{groupId}")
    public ResponseEntity<?> studentsOfGroup(@PathVariable Long groupId,
                                             @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "6") int size) throws Exception {
        return ResponseEntity.ok(userService.getStudentsOfGroup(groupId, page, size));
    }
}
