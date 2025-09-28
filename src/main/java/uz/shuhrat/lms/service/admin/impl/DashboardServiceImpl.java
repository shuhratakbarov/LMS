package uz.shuhrat.lms.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.enums.Role;
import uz.shuhrat.lms.db.repository.admin.DashboardRepository;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.helper.SecurityHelper;
import uz.shuhrat.lms.service.admin.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final DashboardRepository dashboardRepository;

    @Autowired
    public DashboardServiceImpl(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public GeneralResponseDto<?> getGeneralStats() throws Exception {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Authentifikatsiyadan o'ting!");
        }
        if (currentUser.isActive() && currentUser.getRole() == Role.ADMIN) {
            return new GeneralResponseDto<>(true, "General stats data", dashboardRepository.getGeneralStats());
        } else {
            return new GeneralResponseDto<>(false, "You aren't allowed to get the data");
        }
    }
}
