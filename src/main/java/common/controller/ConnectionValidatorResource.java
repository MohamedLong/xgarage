package common.controller;

import common.dto.ConnValidationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class ConnectionValidatorResource {

    @GetMapping("/validateToken")
    public ResponseEntity<ConnValidationResponse> validateGet(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String status = (String) request.getAttribute("status");
        Long userId = (Long) request.getAttribute("id");
        Long tenant = (Long) request.getAttribute("tenant");
        Long tenantType = (Long) request.getAttribute("tenantType");
        List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) request.getAttribute("authorities");
        return ResponseEntity.ok(ConnValidationResponse.builder().status(Boolean.parseBoolean(status)).id(userId)
                .tenant(tenant)
                .tenantType(tenantType)
                .userId(username).authorities(grantedAuthorities)
                .isAuthenticated(true).build());
    }

}

