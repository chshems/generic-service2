package com.mycompany.smp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignupRequestDTO {

    @NotNull
    @Size(max = 50)
    @Email
    private String email;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private Set<String> roles;

    @NotNull
    @Size(min = 6, max = 40)
    private String password;
}
