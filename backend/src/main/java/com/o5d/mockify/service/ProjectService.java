/* (C)2025 */
package com.o5d.mockify.service;

import com.o5d.mockify.model.Project;
import com.o5d.mockify.repository.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public List<Project> getAllActiveProjects() {
        return projectRepository.findByStatusTrue();
    }

    public List<Project> getPublicProjects() {
        return projectRepository.findByStatusTrueAndOpenAccessTrue();
    }

    public List<Project> getProjectsForUser(Long userId, boolean isAdmin) {
        if (isAdmin) {
            return getAllActiveProjects();
        }
        List<Project> owner = projectRepository.findByOwnerIdAndStatusTrue(userId);
        List<Project> team = projectRepository.findByTeamIdAndStatusTrue(userId);
        return Stream.concat(owner.stream(), team.stream()).distinct().toList();
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findByIdAndStatusTrue(id);
    }

    public Optional<Project> findByEndpointId(Long id) {
        return projectRepository.findByIdAndStatusTrue(id);
    }

    public Project save(Project project) {
        project.setStatus(true);
        return projectRepository.save(project);
    }

    public void softDelete(Long id) {
        projectRepository
                .findByIdAndStatusTrue(id)
                .ifPresent(
                        p -> {
                            p.setStatus(false);
                            projectRepository.save(p);
                        });
    }
}
