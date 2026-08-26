package com.mycompany.smp.service;

import com.mycompany.smp.entity.UserEntity;
import com.mycompany.smp.repository.UserRepository;
import org.springframework.context.annotation.Lazy; // 🛑 Import this
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    // ⚡ FIX: Adding @Lazy to PasswordEncoder breaks the circular reference loop!
    public UserServiceImpl(@Lazy PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));

        return UserDetailsImpl.build(user);
    }

    public String addUser(UserEntity userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userRepository.save(userInfo);
        return "User Added Successfully";
    }
}
