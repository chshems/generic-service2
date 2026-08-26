package com.mycompany.smp.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mycompany.smp.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nationality;
    private String password;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private String gender;

    // 🛑 ADD THIS ROW: Tracks account activation status
    private boolean active;

    private Collection<? extends GrantedAuthority> authorities;

    // ⚡ Update Constructor parameters to incorporate the new 'active' status flag
    public UserDetailsImpl(Long id, String firstName, String lastName, String email, String password,
                           String phone, LocalDate dob, String nationality, String gender, boolean active,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.nationality = nationality;
        this.active = active; // Map variable payload parameters
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UserEntity user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                user.getDob(),
                user.getNationality(),
                user.getGender(),
                user.isActive(), // 🛑 PASS THE ACTIVE PROPERTY: Reads your custom boolean flag column
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 🛑 ADD THESE MISSING MANDATORY METHODS FOR SPRING SECURITY:
    @Override
    public boolean isEnabled() {
        return active; // Evaluates user account status dynamically from your database row!
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Enforce true fallback status values
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Enforce true fallback status values
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Enforce true fallback status values
    }

    // --- Keep your existing getters and setters below untouched ---
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setPassword(String password) { this.password = password; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) { this.authorities = authorities; }
}
