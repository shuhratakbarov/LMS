package uz.shuhrat.lms.controller.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.shuhrat.lms.service.admin.DashboardService;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardRestController {
    private final DashboardService dashboardService;
    @Autowired
    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping
    public ResponseEntity<?> getGeneralStats() throws Exception {
        return ResponseEntity.ok(dashboardService.getGeneralStats());
    }
}
