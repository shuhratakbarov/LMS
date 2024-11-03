package uz.farobiy.lms_clone.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.farobiy.lms_clone.db.domain.User;
import uz.farobiy.lms_clone.db.repository.admin.DashboardRepository;
import uz.farobiy.lms_clone.dto.ResponseDto;
import uz.farobiy.lms_clone.helper.SecurityHelper;
import uz.farobiy.lms_clone.service.admin.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final DashboardRepository dashboardRepository;
    @Autowired
    public DashboardServiceImpl(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public ResponseDto<?> getGeneralStats() {
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null && currentUser.isActive() && currentUser.getRole().getId() == 1) {
            return new ResponseDto<>(true, "General stats data", dashboardRepository.getGeneralStats());
        } else {
            return new ResponseDto<>(false, "You aren't allowed to get the data");
        }
    }
}
