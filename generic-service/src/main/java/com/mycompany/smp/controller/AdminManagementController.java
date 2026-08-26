package com.mycompany.smp.controller;

import com.mycompany.smp.constant.ERole;
import com.mycompany.smp.dto.ErrorDTO;
import com.mycompany.smp.entity.UserEntity;
import com.mycompany.smp.exception.BusinessException;
import com.mycompany.smp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')") // Secures the entire controller for ADMIN actors
public class AdminManagementController {

    @Autowired
    private UserRepository userRepository;

    // 🔍 1. FETCH ALL REGISTERED PLATFORM SERVICE PROVIDERS
    @GetMapping("/providers")
    public ResponseEntity<List<UserEntity>> getAllProviders() {
        // Filter users to only include those who have the PROVIDER role assignment
        List<UserEntity> providers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == ERole.ROLE_PROVIDER))
                .collect(Collectors.toList());

        return new ResponseEntity<>(providers, HttpStatus.OK);
    }

    // 🎛️ 2. TOGGLE PROVIDER ACTIVATION STATUS (ACTIVATE / DEACTIVATE)
    @PutMapping("/providers/{id}/toggle")
    public ResponseEntity<?> toggleProviderStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(List.of(new ErrorDTO("USER_NOT_FOUND", "The requested user account does not exist."))));

        Boolean nextStatus = payload.get("active");

        // 💡 Ensure your UserEntity has a boolean field named 'active' or 'enabled'
        // If your UserEntity uses standard Spring Security 'enabled' field instead, use: user.setEnabled(nextStatus);
        user.setActive(nextStatus);

        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
