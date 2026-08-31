package com.example.booking.config;

import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.entity.Resource;
import com.example.booking.repository.UserRepository;
import com.example.booking.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!prod")
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${seed.admin.password}")
    private String seedAdminPassword;

    @Value("${seed.user.password}")
    private String seedUserPassword;

    @Bean
    CommandLineRunner initData(UserRepository userRepository, ResourceRepository resourceRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            logger.info("Initializing application seed data...");
            logger.info("Dev Seed Admin Password: {}", seedAdminPassword);
            logger.info("Dev Seed User Password: {}", seedUserPassword);

            // Seed Users
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode(seedAdminPassword));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                logger.info("Seeded default admin user successfully.");
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode(seedUserPassword));
                user.setRole(Role.USER);
                userRepository.save(user);
                logger.info("Seeded default user user successfully.");
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
                logger.info("Seeded default bookable resources successfully.");
            }
        };
    }
}
