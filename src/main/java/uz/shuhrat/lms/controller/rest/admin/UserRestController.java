package uz.shuhrat.lms.controller.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.dto.request.UserRequestDto;
import uz.shuhrat.lms.service.admin.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/admin/user")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false, name = "role") String role,
            @RequestParam(required = false, name = "searching") String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(userService.getUserList(role, keyword, page, size));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody UserRequestDto form) throws Exception {
        return ResponseEntity.ok(userService.save(form));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable(name = "id") UUID id,
                                  @RequestBody UserRequestDto form) throws Exception {
        return ResponseEntity.ok(userService.edit(id, form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) throws Exception {
        return ResponseEntity.ok(userService.delete(id));
    }
}
