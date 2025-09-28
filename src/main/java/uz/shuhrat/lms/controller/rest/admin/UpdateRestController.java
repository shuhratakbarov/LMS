package uz.shuhrat.lms.controller.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.db.domain.Update;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.UpdateRequestDto;
import uz.shuhrat.lms.dto.response.UpdateResponseDto;
import uz.shuhrat.lms.service.admin.UpdateService;

import java.util.List;

@RestController
@RequestMapping("/admin/update")
@RequiredArgsConstructor
public class UpdateRestController {

    private final UpdateService updateService;

    @PostMapping
    public ResponseEntity<GeneralResponseDto<UpdateResponseDto>> create(
            @RequestBody UpdateRequestDto dto) {
        return ResponseEntity.ok(updateService.save(null, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponseDto<UpdateResponseDto>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(updateService.getById(id));
    }

    @GetMapping
    public ResponseEntity<GeneralResponseDto<List<UpdateResponseDto>>> getAll() {
        return ResponseEntity.ok(updateService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponseDto<UpdateResponseDto>> update(
            @PathVariable Long id,
            @RequestBody UpdateRequestDto dto) {
        return ResponseEntity.ok(updateService.save(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponseDto<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(updateService.delete(id));
    }
}
