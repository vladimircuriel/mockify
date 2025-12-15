/* (C)2025 */
package com.o5d.mockify.controller;

import com.o5d.mockify.dto.request.UserRequestDTO;
import com.o5d.mockify.dto.response.UserResponseDTO;
import com.o5d.mockify.enums.ERole;
import com.o5d.mockify.exception.BadRequestException;
import com.o5d.mockify.exception.NoContentException;
import com.o5d.mockify.exception.ResourceNotFoundException;
import com.o5d.mockify.mapper.UserMapper;
import com.o5d.mockify.model.User;
import com.o5d.mockify.service.JWTService;
import com.o5d.mockify.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;

    private Claims requireValidClaims(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token");
        }

        String jwt = header.substring(7);

        Claims claims =
                jwtService
                        .getClaims(jwt)
                        .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (jwtService.isTokenExpired(jwt)) {
            throw new BadRequestException("Token expired");
        }

        return claims;
    }

    private User getLoggedUser(Claims claims) {
        String username = claims.get("username", String.class);

        return userService
                .getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void requireAdmin(User user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals(ERole.ADMIN));

        if (!isAdmin) {
            throw new BadRequestException("Unauthorized");
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);
        requireAdmin(loggedUser);

        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) throw new NoContentException("No Users Found");

        List<UserResponseDTO> response = UserMapper.INSTANCE.usersToResponseDtos(users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);
        requireAdmin(loggedUser);

        User user =
                userService
                        .getUserById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(UserMapper.INSTANCE.userToResponseDto(user));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO request,
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);
        requireAdmin(loggedUser);

        if (userService.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = UserMapper.INSTANCE.dtoToUser(request);

        User saved = userService.createUser(user, request.getRoles());

        return new ResponseEntity<>(
                UserMapper.INSTANCE.userToResponseDto(saved), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request,
            @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);
        requireAdmin(loggedUser);

        User existing =
                userService
                        .getUserById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userService.existsByUsername(request.getUsername())
                && !existing.getUsername().equals(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userService.existsByEmail(request.getEmail())
                && !existing.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User updated = UserMapper.INSTANCE.dtoToUser(request);
        updated.setId(id);
        updated.setPassword(existing.getPassword());


        User saved = userService.updateUser(updated, request.getRoles());

        return ResponseEntity.ok(UserMapper.INSTANCE.userToResponseDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {

        Claims claims = requireValidClaims(token);
        User loggedUser = getLoggedUser(claims);
        requireAdmin(loggedUser);

        if (loggedUser.getId().equals(id)) {
            throw new BadRequestException("Cannot delete yourself");
        }

        User deleted =
                userService
                        .deleteUser(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(UserMapper.INSTANCE.userToResponseDto(deleted));
    }
}
