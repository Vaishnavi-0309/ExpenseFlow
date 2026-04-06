package projects.springboot.expensetracker.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projects.springboot.expensetracker.service.DashBoardService;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashBoardController {

    private DashBoardService dashBoardService;

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashBoardData(){
        Map<String,Object> dashboardData=dashBoardService.getDashBoardData();
        return ResponseEntity.ok(dashboardData);
    }

}
