package com.mycompany.smp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String nationality;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private String gender;
    private String password;
    private String kykNumber;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // 🛑 ADD THIS LINE HERE: Defaults new accounts to active status
    // Inside your UserEntity.java file:
    @Column(name = "active", nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_to_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();
}
