package com.mycompany.smp.controller;

import com.mycompany.smp.dto.ErrorDTO;
import com.mycompany.smp.entity.CategoryEntity;
import com.mycompany.smp.exception.BusinessException;
import com.mycompany.smp.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController // 🛑 FIX: Restores the missing Rest Controller annotation to clear the error!
@RequestMapping("/api/v1/services") // 🛑 FIX: Restores the missing Request Mapping path route prefix!
public class ServiceController {

    // 🛡️ Declare dependency final (Immutable and highly secure)
    private final CategoryRepository categoryRepository;

    private static final List<Map<String, Object>> mockServiceDatabase = new ArrayList<>();
    private static long idCounter = 101;

    // ⚡ FIX: Constructor Injection resolves your remaining Field Injection warnings entirely!
    public ServiceController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Static helper method allowing your Startup runner to easily add entries on boot
    public static void seedInitialService(Long id, String name, String description, Double price, Long categoryId, String categoryName) {
        Map<String, Object> service = new HashMap<>();
        service.put("id", id);
        service.put("name", name);
        service.put("description", description);
        service.put("price", price);
        service.put("categoryId", categoryId);
        service.put("categoryName", categoryName);
        service.put("paused", false);

        mockServiceDatabase.add(service);
    }

    // Permits both ADMIN and PROVIDER actors to read services layout records
    @PreAuthorize("hasAuthority('ROLE_PROVIDER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllServices() {
        return new ResponseEntity<>(mockServiceDatabase, HttpStatus.OK);
    }

    // Restricts service creation strictly to PROVIDER profiles
    @PreAuthorize("hasAuthority('ROLE_PROVIDER')")
    @PostMapping
    public ResponseEntity<?> createService(@RequestBody Map<String, Object> serviceRequest) {
        String name = (String) serviceRequest.get("name");
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(List.of(new ErrorDTO("name", "Service title cannot be left blank.")));
        }
        if (serviceRequest.get("price") == null) {
            throw new BusinessException(List.of(new ErrorDTO("price", "Operational service rate price is required.")));
        }

        Long categoryId = Long.valueOf(serviceRequest.get("categoryId").toString());
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(List.of(new ErrorDTO("categoryId", "The selected category does not exist."))));

        Map<String, Object> newService = new HashMap<>();
        newService.put("id", idCounter++);
        newService.put("name", name);
        newService.put("description", (String) serviceRequest.get("description"));
        newService.put("price", Double.parseDouble(serviceRequest.get("price").toString()));
        newService.put("categoryId", categoryId);
        newService.put("categoryName", category.getName());
        newService.put("paused", false);

        mockServiceDatabase.add(newService);
        return new ResponseEntity<>(newService, HttpStatus.CREATED);
    }

    // Permits both ADMIN and PROVIDER actor profiles to pause or unpause execution states
    @PreAuthorize("hasAuthority('ROLE_PROVIDER') or hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{serviceId}/pause")
    public ResponseEntity<?> togglePauseService(@PathVariable Long serviceId, @RequestBody Map<String, Object> pausePayload) {
        Boolean pausedStatus = (Boolean) pausePayload.get("paused");

        for (Map<String, Object> service : mockServiceDatabase) {
            if (service.get("id").toString().equals(serviceId.toString())) {
                service.put("paused", pausedStatus);
                return new ResponseEntity<>(service, HttpStatus.OK);
            }
        }
        throw new BusinessException(List.of(new ErrorDTO("SERVICE_NOT_FOUND", "Requested service workspace ID does not exist.")));
    }
}
