/* (C)2025 */
package com.o5d.mockify.repository;

import com.o5d.mockify.enums.ERole;
import com.o5d.mockify.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
