/* (C)2025 */
package com.o5d.mockify.config;

import com.o5d.mockify.enums.ERole;
import com.o5d.mockify.model.Role;
import com.o5d.mockify.model.User;
import com.o5d.mockify.repository.RoleRepository;
import com.o5d.mockify.repository.UserRepository;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.count() == 0) {

            Role adminRole =
                    roleRepository
                            .findByName(ERole.ADMIN)
                            .orElseGet(() -> roleRepository.save(new Role(ERole.ADMIN)));

            User admin =
                    User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin"))
                            .firstName("Admin")
                            .lastName("Admin")
                            .email("admin@admin.com")
                            .active(true)
                            .roles(Set.of(adminRole))
                            .build();

            userRepository.save(admin);
        }
    }
}
