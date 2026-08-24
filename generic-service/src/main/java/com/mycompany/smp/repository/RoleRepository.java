package com.mycompany.smp.repository;

import com.mycompany.smp.constant.ERole;
import com.mycompany.smp.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole name);
}
