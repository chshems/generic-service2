package com.mycompany.smp.controller;

import com.mycompany.smp.constant.ERole;
import com.mycompany.smp.dto.*;
import com.mycompany.smp.entity.RoleEntity;
import com.mycompany.smp.entity.UserEntity;
import com.mycompany.smp.exception.BusinessException;
import com.mycompany.smp.repository.RoleRepository;
import com.mycompany.smp.repository.UserRepository;
import com.mycompany.smp.service.JwtServiceImpl;
import com.mycompany.smp.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/noauth")
public class AuthController {

    // 🛡️ All dependencies declared as final (Immutable and clean)
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtServiceImpl jwtService;

    // ⚡ Constructor Injection: Resolves the field injection warning entirely!
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder encoder,
                          JwtServiceImpl jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponseDTO(
                jwt,
                userDetails.getId(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getEmail(),
                roles
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDTO signupRequest) {
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            ErrorDTO err = new ErrorDTO();
            err.setCode("AUTH_005");
            err.setMessage("Email is already taken");
            throw new BusinessException(List.of(err));
        }

        UserEntity user = new UserEntity();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));

        // Match structural fields from your UserEntity schema specifications
        user.setActive(true);

        Set<String> strRoles = signupRequest.getRoles();
        Set<RoleEntity> roles = new HashSet<>();

        if(strRoles == null) {
            RoleEntity userRole = roleRepository.findByName(ERole.ROLE_CONSUMER)
                    .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "ROLE_CONSUMER":
                        RoleEntity modRole = roleRepository.findByName(ERole.ROLE_CONSUMER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    case "ROLE_PROVIDER" :
                        RoleEntity managerRole = roleRepository.findByName(ERole.ROLE_PROVIDER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(managerRole);
                        break;
                    default:
                        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_CONSUMER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new MessageResponseDTO("User registered successfully!"), HttpStatus.CREATED);
    }
}
