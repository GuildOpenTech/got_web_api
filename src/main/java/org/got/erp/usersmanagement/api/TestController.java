package org.got.erp.usersmanagement.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.erp.usersmanagement.dto.CustomUserDetails;
import org.got.erp.usersmanagement.entity.User;
import org.got.erp.usersmanagement.service.ACLService;
import org.got.erp.usersmanagement.service.TestDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test", description = "Test endpoints")
public class TestController {
    private final TestDataService testDataService;
    private final ACLService aclService;

    @GetMapping("/permissions")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> testPermissions(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.user();

        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put("can_read_users",
                aclService.hasPermission(user, "USER", "READ"));
        permissions.put("can_create_user",
                aclService.hasPermission(user, "USER", "CREATE"));
        permissions.put("can_update_user",
                aclService.hasPermission(user, "USER", "UPDATE"));
        permissions.put("can_delete_user",
                aclService.hasPermission(user, "USER", "DELETE"));

        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/sample-data")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createSampleData() {
        try {
            testDataService.createSampleData();
            return ResponseEntity.ok("Sample data created successfully");
        } catch (Exception e) {
            log.error("Error creating sample data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating sample data: " + e.getMessage());
        }
    }
}
