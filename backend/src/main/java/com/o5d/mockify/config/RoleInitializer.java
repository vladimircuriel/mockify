/* (C)2025 */
package com.o5d.mockify.config;

import com.o5d.mockify.enums.ERole;
import com.o5d.mockify.model.Role;
import com.o5d.mockify.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (ERole erole : ERole.values()) {
            roleRepository.findByName(erole).orElseGet(() -> roleRepository.save(new Role(erole)));
        }
    }
}
