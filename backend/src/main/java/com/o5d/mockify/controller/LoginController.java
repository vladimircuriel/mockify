/* (C)2025 */
package com.o5d.mockify.controller;

import com.o5d.mockify.dto.request.LoginRequestDTO;
import com.o5d.mockify.dto.response.AuthResponseDTO;
import com.o5d.mockify.exception.ResourceNotFoundException;
import com.o5d.mockify.exception.UnauthorizedException;
import com.o5d.mockify.model.User;
import com.o5d.mockify.service.JWTService;
import com.o5d.mockify.service.UserService;
import com.o5d.mockify.utils.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/login")
public class LoginController {

    private final UserService userService;
    private final JWTService jwtService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        User user =
                userService
                        .getUserByUsername(dto.getUsername())
                        .orElseThrow(() -> new ResourceNotFoundException("Wrong Credentials"));

        if (!Utils.isPasswordCorrect(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Wrong Credentials");
        }

        AuthResponseDTO token = jwtService.generateToken(dto.getUsername());
        return ResponseEntity.ok(token);
    }
}
