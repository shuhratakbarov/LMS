package uz.shuhrat.lms.service.admin.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.domain.Update;
import uz.shuhrat.lms.db.repository.UpdateRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.UpdateRequestDto;
import uz.shuhrat.lms.dto.response.UpdateResponseDto;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.service.admin.UpdateService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateServiceImpl implements UpdateService {

    private final UpdateRepository updateRepository;

    @Override
    public GeneralResponseDto<UpdateResponseDto> save(Long id, UpdateRequestDto dto) {
        Update update = new Update();

        if (dto.role() == null || dto.role().isBlank()) {
            return new GeneralResponseDto<>(false, "Role is not presented");
        } else {
            if (id != null) {
                update = updateRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Update not found"));
            }
            update.setTitle(dto.title());
            update.setBody(dto.body());
            update.setType(dto.type());

            String normalized = dto.role().trim().toUpperCase();
            switch (normalized) {
                case "TEACHER", "TEACHERS" -> update.setRoles(List.of(Role.TEACHER));
                case "STUDENT", "STUDENTS" -> update.setRoles(List.of(Role.STUDENT));
                case "ADMIN", "ADMINS" -> update.setRoles(List.of(Role.ADMIN));
                case "ALL", "ALL USERS" -> update.setRoles(Arrays.asList(Role.TEACHER, Role.STUDENT));
                default -> throw new IllegalArgumentException("Invalid role: " + dto.role());
            }
        }

        Update saved = updateRepository.save(update);
        return new GeneralResponseDto<>(true, "ok", toResponseDto(saved));
    }


    @Override
    public GeneralResponseDto<UpdateResponseDto> getById(Long id) {
        Update update = updateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Update not found"));
        return new GeneralResponseDto<>(true, "ok", toResponseDto(update));
    }

    @Override
    public GeneralResponseDto<List<UpdateResponseDto>> getAll() {
        List<UpdateResponseDto> updates = updateRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
        return new GeneralResponseDto<>(true, "ok", updates);
    }

    @Override
    public GeneralResponseDto<List<UpdateResponseDto>> getByRole(Role role) {
        List<UpdateResponseDto> updates = updateRepository.findByRole(role.toString())
                .stream()
                .map(this::toResponseDto)
                .toList();
        return new GeneralResponseDto<>(true, "ok", updates);
    }

    @Override
    public GeneralResponseDto<?> delete(Long id) {
        updateRepository.deleteById(id);
        return new GeneralResponseDto<>(true, "ok");
    }

    private UpdateResponseDto toResponseDto(Update update) {
        return new UpdateResponseDto(
                update.getId(),
                update.getTitle(),
                update.getBody(),
                update.getType(),
                update.getRoles(),
                update.getCreatedAt(),
                update.getUpdatedAt()
        );
    }
}

