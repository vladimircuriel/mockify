/* (C)2025 */
package com.o5d.mockify.controller;

import com.o5d.mockify.exception.BadRequestException;
import com.o5d.mockify.service.JWTService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/logout/")
public class LogoutController {

    private final JWTService jwtService;

    @PostMapping
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") @NotBlank String header) {

        if (!header.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token");
        }

        String jwt = header.substring(7);

        jwtService.getClaims(jwt).orElseThrow(() -> new BadRequestException("Invalid token"));

        if (jwtService.isTokenExpired(jwt)) {
            throw new BadRequestException("Token expired");
        }

        jwtService.revokeToken(jwt);

        return ResponseEntity.ok().build();
    }
}
