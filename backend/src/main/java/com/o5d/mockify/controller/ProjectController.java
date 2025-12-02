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
import java.util.Optional;
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
@RequestMapping("/api/v1/projects/")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final JWTService jwtService;

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(
            @RequestHeader("Authorization") String token) {

        String jwt = token.substring(7);
        Optional<Claims> claims = jwtService.getClaims(jwt);
        if (claims.isEmpty()) throw new BadRequestException("Invalid token");
        if (jwtService.isTokenExpired(jwt)) throw new BadRequestException("Token expired");

        String username = claims.get().get("username", String.class);
        User loggedUser =
                userService
                        .getUserByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin =
                loggedUser.getRoles().stream().anyMatch(r -> r.getName().equals(ERole.ADMIN));

        List<Project> projects = projectService.getProjectsForUser(loggedUser.getId(), isAdmin);

        if (projects.isEmpty()) throw new ResourceNotFoundException("No projects found");

        List<ProjectResponseDTO> response =
                projects.stream().map(ProjectMapper.INSTANCE::projectToResponseDto).toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("public/")
    public ResponseEntity<List<ProjectResponseDTO>> getPublicProjects() {

        List<Project> projects = projectService.getPublicProjects();

        List<ProjectResponseDTO> response =
                projects.stream().map(ProjectMapper.INSTANCE::projectToResponseDto).toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {

        String jwt = token.substring(7);
        Optional<Claims> claims = jwtService.getClaims(jwt);
        if (claims.isEmpty()) throw new BadRequestException("Invalid token");
        if (jwtService.isTokenExpired(jwt)) throw new BadRequestException("Token expired");

        String username = claims.get().get("username", String.class);
        User loggedUser =
                userService
                        .getUserByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project =
                projectService
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isOwner = project.getOwner().getUsername().equals(username);
        boolean isPublic = project.isOpenAccess();
        boolean isTeam = project.getTeam().stream().anyMatch(u -> u.getUsername().equals(username));
        boolean isAdmin =
                loggedUser.getRoles().stream().anyMatch(r -> r.getName().equals(ERole.ADMIN));

        if (!isOwner && !isPublic && !isTeam && !isAdmin)
            throw new ForbiddenException("You are not allowed to view this project");

        ProjectResponseDTO response = ProjectMapper.INSTANCE.projectToResponseDto(project);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        String jwt = token.substring(7);
        Optional<Claims> claims = jwtService.getClaims(jwt);
        if (claims.isEmpty()) throw new BadRequestException("Invalid token");
        if (jwtService.isTokenExpired(jwt)) throw new BadRequestException("Token expired");

        String username = claims.get().get("username", String.class);
        User loggedUser =
                userService
                        .getUserByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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

    @PatchMapping("{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        String jwt = token.substring(7);
        Optional<Claims> claims = jwtService.getClaims(jwt);
        if (claims.isEmpty()) throw new BadRequestException("Invalid token");
        if (jwtService.isTokenExpired(jwt)) throw new BadRequestException("Token expired");

        String username = claims.get().get("username", String.class);
        Project project =
                projectService
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getUsername().equals(username))
            throw new ForbiddenException("You are not the owner of the project");

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setTag(dto.getTag());
        project.setOpenAccess(dto.isOpenAccess());

        Project saved = projectService.save(project);

        ProjectResponseDTO response = ProjectMapper.INSTANCE.projectToResponseDto(saved);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {

        projectService.softDelete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
