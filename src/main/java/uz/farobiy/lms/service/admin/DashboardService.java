package uz.farobiy.lms.service.admin;

import uz.farobiy.lms.dto.ResponseDto;

public interface DashboardService {
    ResponseDto<?> getGeneralStats();
}
