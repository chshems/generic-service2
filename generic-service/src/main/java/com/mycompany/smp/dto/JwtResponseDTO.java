package com.mycompany.smp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor  // Crucial for Jackson JSON serialization/deserialization
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer"; // Safely defaults to Bearer
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;

    // Custom constructor matching your explicit parameters
    public JwtResponseDTO(String token, Long id, String firstName, String lastName, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }
}
