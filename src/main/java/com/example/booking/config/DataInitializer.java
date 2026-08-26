package com.example.booking.config;

import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.entity.Resource;
import com.example.booking.repository.UserRepository;
import com.example.booking.repository.ResourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, ResourceRepository resourceRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Users
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user"));
                user.setRole(Role.USER);
                userRepository.save(user);
            }
            
            // Seed Resources
            if (resourceRepository.findAll().isEmpty()) {
                Resource res1 = new Resource();
                res1.setName("Conference Room A");
                res1.setDescription("Large conference room with projector and whiteboard");
                resourceRepository.save(res1);

                Resource res2 = new Resource();
                res2.setName("Projector 4K");
                res2.setDescription("High-resolution 4K projector for presentations");
                resourceRepository.save(res2);
            }
        };
    }
}
