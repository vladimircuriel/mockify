package com.o5d.mockify.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.o5d.mockify.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(boolean status);

    Optional<Project> findByIdAndStatus(Long id, boolean status);

    List<Project> findByOwnerUsername(String username);

    List<Project> findByStatusAndOpenAccess(boolean status, boolean isPublic);

    List<Project> findByOwnerIdAndStatus(Long id, boolean status);

    List<Project> findByTeamIdAndStatus(Long id, boolean status);
}