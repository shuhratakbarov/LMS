package uz.shuhrat.lms.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uz.shuhrat.lms.dto.form.CreateGroupForm;
import uz.shuhrat.lms.service.admin.GroupService;
import uz.shuhrat.lms.service.admin.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/admin/group")
public class GroupRestController {
    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public GroupRestController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getListGroup(@RequestParam(required = false, defaultValue = "0") int page,
                                          @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(groupService.findAll(page, size));
    }

    @GetMapping("/group-id-and-name")
    public ResponseEntity<?> findGroupsForSelect() {
        return ResponseEntity.ok(groupService.getGroupIdAndName());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupForm form) throws Exception {
        return ResponseEntity.ok(groupService.save(form));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editGroup(@PathVariable Long id,
                                       @RequestBody CreateGroupForm form) throws Exception {
        return ResponseEntity.ok(groupService.edit(id, form));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(groupService.delete(id));
    }

    @GetMapping("/students-of-group/{groupId}")
    public ResponseEntity<?> studentsOfGroup(@PathVariable Long groupId,
                                             @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "6") int size) throws Exception {
        return ResponseEntity.ok(userService.getStudentsOfGroup(groupId, page, size));
    }

    @GetMapping("/groups-of-teacher/{teacherId}")
    public ResponseEntity<?> groupsOfTeacher(@PathVariable UUID teacherId) throws Exception {
        return ResponseEntity.ok(groupService.findGroupsByTeacherId(teacherId));
    }

    @GetMapping("/groups-of-student/{studentId}")
    public ResponseEntity<?> groupsOfStudent(@PathVariable UUID studentId) throws Exception {
        return ResponseEntity.ok(groupService.getGroupsAndTeacherByStudentId(studentId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> getSearchGroupList(@RequestParam(name = "searching") String keyword,
                                                @RequestParam(required = false, defaultValue = "0") int page,
                                                @RequestParam(required = false, defaultValue = "20") int size) throws Exception {
        return ResponseEntity.ok(groupService.search(keyword, page, size));
    }

    @PostMapping("/add-student")
    public ResponseEntity<?> addStudent(@RequestParam("student-id") UUID studentId,
                                        @RequestParam("group-id") Long groupId) throws Exception {
        return ResponseEntity.ok(groupService.addStudentToGroup(studentId, groupId));
    }

    @DeleteMapping("/remove-student/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable("studentId") UUID studentId,
                                           @RequestParam("group-id") Long groupId) {
        return ResponseEntity.ok(groupService.removeStudentFromGroup(studentId, groupId));
    }

    @GetMapping("/search-student")
    public ResponseEntity<?> searchStudent(@RequestParam("username") String username) throws Exception {
        return ResponseEntity.ok(userService.searchStudent(username));
    }
}
