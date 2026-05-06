package com.bank.pfe1;

import com.bank.pfe1.entity.Role;
import com.bank.pfe1.entity.User;
import com.bank.pfe1.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bank.pfe1"})
@EnableScheduling

public class Pfe1Application {

    public static void main(String[] args) {
        SpringApplication.run(Pfe1Application.class, args);
    }

    @Bean
    CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .fullName("Administrator")
                        .username("admin")
                        .email("admin@bank.dz")
                        .password(passwordEncoder.encode("admin"))
                        .role(Role.ADMIN)
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Default admin user created: username=admin, password=admin");
            }
        };
    }
}

