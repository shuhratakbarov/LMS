package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.UpdateRequestDto;
import uz.shuhrat.lms.dto.response.UpdateResponseDto;
import uz.shuhrat.lms.enums.Role;

import java.util.List;

public interface UpdateService {
    GeneralResponseDto<UpdateResponseDto> save(Long id, UpdateRequestDto dto);

    GeneralResponseDto<UpdateResponseDto> getById(Long id);

    GeneralResponseDto<List<UpdateResponseDto>> getAll();

    GeneralResponseDto<List<UpdateResponseDto>> getByRole(Role role);

    GeneralResponseDto<?> delete(Long id);
}

