package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.DashboardResponse;
import com.amar.fullstack.expanse_tracker_backend.dtos.UserResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Role;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import com.amar.fullstack.expanse_tracker_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final UserRepository userRepository;
    private final UserService userService;
    private final DashboardService dashboardService;

    public AdminController(UserRepository userRepository,
                           UserService userService,
                           DashboardService dashboardService){
        this.userRepository=userRepository;
        this.userService=userService;
        this.dashboardService=dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String , Long>> getAdminDashboard(){

        long totalUsers=userRepository.count();
        long activeUsers=userRepository.countByIsActiveTrue();
        long premiumUsers=userRepository.countByIsPremiumTrue();
        long adminUsers=userRepository.countByRole(Role.ADMIN);

        return ResponseEntity.ok(
                Map.of("totalUsers: ", totalUsers,
                        "activeUsers: ", activeUsers,
                        "premiumUsers: ", premiumUsers,
                        "adminUsers: ", adminUsers)
        );
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> findAll(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/users/{userId}/dashboard")
    public ResponseEntity<DashboardResponse> getSummaryByUserId(@PathVariable Long userId){
        DashboardResponse response= dashboardService.getSummaryByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> editRole(@PathVariable Long id,
                                                    @RequestParam String role){
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId){
        userService.deleteUserById(userId);
        return ResponseEntity.ok("User Successfully deleted");
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active
    ){
        return ResponseEntity.ok(
                userService.updateUserStatus(userId, active)
        );
    }
}
