package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.ResponseDto;

public interface DashboardService {
    ResponseDto<?> getGeneralStats();
}
