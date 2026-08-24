package com.mycompany.smp;

import com.mycompany.smp.constant.ERole;
import com.mycompany.smp.entity.RoleEntity;
import com.mycompany.smp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Startup implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... arg) throws Exception {
        Optional<RoleEntity> optRoleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN);
        if(optRoleAdmin.isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_ADMIN);
            roleRepository.save(role);
        }
        Optional<RoleEntity> optRoleOwn = roleRepository.findByName(ERole.ROLE_CONSUMER);
        if(optRoleOwn.isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_CONSUMER);
            roleRepository.save(role);
        }

        Optional<RoleEntity> optRoleMe = roleRepository.findByName(ERole.ROLE_PROVIDER);
        if(optRoleMe.isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(ERole.ROLE_PROVIDER);
            roleRepository.save(role);
        }
    }

}
