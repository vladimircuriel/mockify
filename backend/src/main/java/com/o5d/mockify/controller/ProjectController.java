/* (C)2025 */
package com.o5d.mockify.controller;

import com.o5d.mockify.dto.request.ProjectRequestDTO;
import com.o5d.mockify.dto.response.ProjectResponseDTO;
import com.o5d.mockify.enums.ERole;
import com.o5d.mockify.exception.BadRequestException;
import com.o5d.mockify.exception.ForbiddenException;
import com.o5d.mockify.exception.ResourceNotFoundException;
import com.o5d.mockify.mapper.ProjectMapper;
import com.o5d.mockify.model.Project;
import com.o5d.mockify.model.User;
import com.o5d.mockify.service.JWTService;
import com.o5d.mockify.service.ProjectService;
import com.o5d.mockify.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final JWTService jwtService;

    private Claims requireValidClaims(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new BadRequestException("Invalid token");

        String jwt = header.substring(7);

        Claims claims =
                jwtService
                        .getClaims(jwt)
                        .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (jwtService.isTokenExpired(jwt)) throw new BadRequestException("Token expired");

        return claims;
    }

    private User getLoggedUser(Claims claims) {
        String username = claims.get("username", String.class);
        return userService
                .getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(r -> r.getName().equals(ERole.ADMIN));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);
        boolean admin = isAdmin(loggedUser);

        List<Project> projects = projectService.getProjectsForUser(loggedUser.getId(), admin);
        if (projects.isEmpty()) throw new ResourceNotFoundException("No projects found");

        List<ProjectResponseDTO> response =
                projects.stream().map(ProjectMapper.INSTANCE::projectToResponseDto).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProjectResponseDTO>> getPublicProjects() {

        List<Project> projects = projectService.getPublicProjects();

        List<ProjectResponseDTO> response =
                projects.stream().map(ProjectMapper.INSTANCE::projectToResponseDto).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);

        Project project =
                projectService
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean owner = project.getOwner().getId().equals(loggedUser.getId());
        boolean publicAccess = project.isOpenAccess();
        boolean team =
                project.getTeam().stream().anyMatch(u -> u.getId().equals(loggedUser.getId()));
        boolean admin = isAdmin(loggedUser);

        if (!owner && !publicAccess && !team && !admin)
            throw new ForbiddenException("You are not allowed to view this project");

        ProjectResponseDTO response = ProjectMapper.INSTANCE.projectToResponseDto(project);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);

        if (!dto.getOwnerId().equals(loggedUser.getId()))
            throw new ForbiddenException("You are not the owner of the project");

        User owner =
                userService
                        .getUserById(dto.getOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Project project = ProjectMapper.INSTANCE.dtoToProject(dto);
        project.setOwner(owner);

        Project saved = projectService.save(project);
        ProjectResponseDTO response = ProjectMapper.INSTANCE.projectToResponseDto(saved);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);

        Project project =
                projectService
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(loggedUser.getId()))
            throw new ForbiddenException("You are not the owner of the project");

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setTag(dto.getTag());
        project.setOpenAccess(dto.isOpenAccess());

        Project saved = projectService.save(project);
        ProjectResponseDTO response = ProjectMapper.INSTANCE.projectToResponseDto(saved);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/team/{username}")
    public ResponseEntity<ProjectResponseDTO> addUserToTeam(
            @PathVariable Long projectId,
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);

        Project project =
                projectService
                        .findById(projectId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(loggedUser.getId()))
            throw new ForbiddenException("You are not the owner of the project");

        User user =
                userService
                        .getUserByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (loggedUser.getId().equals(user.getId()))
            throw new ForbiddenException("You cannot add yourself to the team");

        project.getTeam().add(user);

        Project saved = projectService.save(project);
        ProjectResponseDTO response = ProjectMapper.INSTANCE.projectToResponseDto(saved);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}/team/{username}")
    public ResponseEntity<ProjectResponseDTO> removeUserFromTeam(
            @PathVariable Long projectId,
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);

        Project project =
                projectService
                        .findById(projectId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(loggedUser.getId()))
            throw new ForbiddenException("You are not the owner of the project");

        User user =
                userService
                        .getUserByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (loggedUser.getId().equals(user.getId()))
            throw new ForbiddenException("You cannot remove yourself from the team");

        project.getTeam().remove(user);

        Project saved = projectService.save(project);
        ProjectResponseDTO response = ProjectMapper.INSTANCE.projectToResponseDto(saved);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);

        Project project =
                projectService
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(loggedUser.getId()) && !isAdmin(loggedUser))
            throw new ForbiddenException("You cannot delete this project");

        projectService.softDelete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
