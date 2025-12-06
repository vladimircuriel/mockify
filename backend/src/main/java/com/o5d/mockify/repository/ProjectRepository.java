/* (C)2025 */
package com.o5d.mockify.repository;

import com.o5d.mockify.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatusTrue();

    List<Project> findByStatusTrueAndOpenAccessTrue();

    Optional<Project> findByIdAndStatusTrue(Long id);

    Optional<Project> findByEndpointsIdAndStatusTrue(Long id);

    List<Project> findByOwnerUsername(String username);

    List<Project> findByOwnerIdAndStatusTrue(Long id);

    List<Project> findByTeamIdAndStatusTrue(Long id);
}
