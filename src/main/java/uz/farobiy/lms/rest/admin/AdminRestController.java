package uz.farobiy.lms.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.farobiy.lms.dto.form.user.CreateUserForm;
import uz.farobiy.lms.service.admin.DashboardService;
import uz.farobiy.lms.service.admin.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminRestController {
    private final UserService userService;
    private final DashboardService dashboardService;

    @Autowired
    public AdminRestController(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getGeneralStats() {
        return ResponseEntity.ok(dashboardService.getGeneralStats());
    }

    @GetMapping("/list/{roleId}")
    public ResponseEntity<?> findAllByRoleId(@PathVariable("roleId") Long roleId,
                                             @RequestParam("activity") String isActive,
                                             @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(userService.findAllByRoleId(roleId, isActive, page, size));
    }

    @GetMapping("/teacher-id-and-username")
    public ResponseEntity<?> findTeachersForSelect() {
        return ResponseEntity.ok(userService.findTeachersForSelect());
    }

    @GetMapping("/search/{roleId}")
    public ResponseEntity<?> search(@PathVariable("roleId") Long roleId,
                                    @RequestParam String searching,
                                    @RequestParam("activity") String isActive,
                                    @RequestParam(required = false, defaultValue = "0") int page,
                                    @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(userService.search(roleId, isActive, searching, page, size));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable UUID id,
                                  @RequestBody CreateUserForm form) throws Exception {
        return ResponseEntity.ok(userService.edit(id, form));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) throws Exception {
        return ResponseEntity.ok(userService.delete(id));
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody CreateUserForm form) throws Exception {
        return ResponseEntity.ok(userService.save(form));
    }
}
