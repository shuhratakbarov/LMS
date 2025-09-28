package uz.shuhrat.lms.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.db.domain.Update;
import uz.shuhrat.lms.service.admin.UpdateService;

import java.util.List;

@RestController
@RequestMapping("/admin/update")
@RequiredArgsConstructor
public class UpdateRestController {

    private final UpdateService updateService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Update update) {
        return ResponseEntity.ok(updateService.save(update));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(updateService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(updateService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Update update) {
        update.setId(id);
        return ResponseEntity.ok(updateService.save(update));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.ok(updateService.delete(id));
    }
}
