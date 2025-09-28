package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.GeneralResponseDto;

public interface DashboardService {
    GeneralResponseDto<?> getGeneralStats() throws Exception;
}
