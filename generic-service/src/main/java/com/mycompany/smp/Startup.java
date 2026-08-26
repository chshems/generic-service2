package com.mycompany.smp;

import com.mycompany.smp.constant.ERole;
import com.mycompany.smp.entity.RoleEntity;
import com.mycompany.smp.entity.UserEntity;
import com.mycompany.smp.repository.RoleRepository;
import com.mycompany.smp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class Startup implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Modern Constructor Injection for clean bean binding
    public Startup(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... arg) throws Exception {
        // 1. Initialize core system roles if they are missing
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_ADMIN);
            roleRepository.save(role);
        }
        if (roleRepository.findByName(ERole.ROLE_CONSUMER).isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_CONSUMER);
            roleRepository.save(role);
        }
        if (roleRepository.findByName(ERole.ROLE_PROVIDER).isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_PROVIDER);
            roleRepository.save(role);
        }

        // 2. Clear out stale unencrypted account columns if they exist
        String adminEmail = "admin@marketplace.com";
        userRepository.findByEmail(adminEmail).ifPresent(userRepository::delete);

        // 3. Build a fresh, cleanly hashed Admin Entity matching your UserEntity variables
        RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Admin Role missing."));

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("System");
        adminUser.setLastName("Admin");
        adminUser.setEmail(adminEmail);
        adminUser.setPhone("0000000000");
        adminUser.setNationality("Local");
        adminUser.setGender("Other");
        adminUser.setDob(LocalDate.of(1990, 1, 1));
        adminUser.setCreatedAt(LocalDate.now());
        adminUser.setUpdatedAt(LocalDate.now());

        // Explicitly enforce active status row check flag
        adminUser.setActive(true);

        // 🛑 CRUCIAL: Encapsulate the clear-text using BCryptPasswordEncoder
        adminUser.setPassword(passwordEncoder.encode("AdminPassword123"));

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(adminRole);
        adminUser.setRoles(roles);

        userRepository.save(adminUser);
        System.out.println(">>> 🏁 Master Admin Seeded Successfully with BCrypt encryption hash: " + adminEmail);


        // Paste this right at the bottom of your run method in Startup.java:

        System.out.println(">>> Seeding cross-platform mock service records...");

// Calls our fresh static method inside ServiceController directly
        com.mycompany.smp.controller.ServiceController.seedInitialService(
                101L,
                "Outdoor Photography Session",
                "Full 2-hour outdoor portrait photography package with premium digital touch-ups.",
                120.00,
                1L, // Temporary mock Category ID linking placeholder
                "Photography two" // Displays the matching name from your database snapshot
        );

        com.mycompany.smp.controller.ServiceController.seedInitialService(
                102L,
                "Bridal Hair & Makeup Styling",
                "Long-lasting professional airbrush application and elegant hair formatting.",
                150.00,
                2L,
                "Beauty and Salon"
        );

        com.mycompany.smp.controller.ServiceController.seedInitialService(
                103L,
                "Sound System Coordination",
                "Premium audio mixer deployment and live microphone management for events.",
                350.00,
                3L,
                "Event Management"
        );

        System.out.println(">>> 🏁 Initial service catalog successfully populated!");

    }
}
