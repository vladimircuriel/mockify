/* (C)2025 */
package com.o5d.mockify.controller;

import com.o5d.mockify.dto.request.EndpointRequestDTO;
import com.o5d.mockify.dto.response.EndpointResponseDTO;
import com.o5d.mockify.exception.BadRequestException;
import com.o5d.mockify.exception.ForbiddenException;
import com.o5d.mockify.exception.InternalServerError;
import com.o5d.mockify.exception.ResourceNotFoundException;
import com.o5d.mockify.mapper.EndpointMapper;
import com.o5d.mockify.mapper.HeaderMapper;
import com.o5d.mockify.model.Endpoint;
import com.o5d.mockify.model.Header;
import com.o5d.mockify.model.Project;
import com.o5d.mockify.repository.ProjectRepository;
import com.o5d.mockify.service.EndpointService;
import com.o5d.mockify.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/endpoint/")
@RequiredArgsConstructor
public class EndpointController {

    private final EndpointService endpointService;
    // private final HeaderService headerService;
    private final ProjectRepository projectRepository;
    private final JWTService jwtService;

    @RequestMapping(
            value = "**",
            method = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.PATCH,
                RequestMethod.OPTIONS
            })
    public ResponseEntity<String> handleDynamicRequest(HttpServletRequest request)
            throws InterruptedException {

        String path = request.getRequestURI().replace("/api/v1/endpoint/", "");
        String method = request.getMethod();

        Endpoint endpoint =
                endpointService
                        .getEndpointByPathAndMethod(path, method)
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint not found"));

        if (endpoint.getExpirationDate() != null
                && endpoint.getExpirationDate().isBefore(LocalDateTime.now())) {
            endpoint.setStatus(false);
            endpointService.saveEndpoint(endpoint);
            throw new ForbiddenException("Endpoint expired");
        }

        if (endpoint.getDelay() > 0) Thread.sleep(endpoint.getDelay() * 1000L);

        int status = Integer.parseInt(endpoint.getResponseStatus());
        MediaType contentType = MediaType.parseMediaType(endpoint.getResponseType());
        String body = endpoint.getBody();

        HttpHeaders headers = new HttpHeaders();
        endpoint.getHeaders().forEach(h -> headers.add(h.getKey(), h.getValue()));

        return ResponseEntity.status(status).contentType(contentType).headers(headers).body(body);
    }

    @GetMapping
    public ResponseEntity<List<EndpointResponseDTO>> getAllEndpoints() {

        List<Endpoint> endpoints = endpointService.getAllEndpoints();
        if (endpoints.isEmpty()) throw new ResourceNotFoundException("No endpoints found");

        List<EndpointResponseDTO> response =
                endpoints.stream().map(EndpointMapper.INSTANCE::endpointToResponseDto).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<EndpointResponseDTO> getEndpointById(@PathVariable Long id) {

        Endpoint endpoint =
                endpointService
                        .getEndpointById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint not found"));

        EndpointResponseDTO response = EndpointMapper.INSTANCE.endpointToResponseDto(endpoint);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EndpointResponseDTO> createEndpoint(
            @Valid @RequestBody EndpointRequestDTO dto) {

        Project project =
                projectRepository
                        .findById(dto.getProjectId())
                        .orElseThrow(() -> new BadRequestException("Project not found"));

        if (endpointService
                .getEndpointByPathAndMethod(dto.getPath(), dto.getMethod())
                .isPresent())
            throw new BadRequestException("Endpoint already exists");

        Endpoint endpoint = EndpointMapper.INSTANCE.dtoToEndpoint(dto);
        endpoint.setProject(project);

        List<Header> headers = HeaderMapper.INSTANCE.requestDtosToHeaders(dto.getHeaders());
        headers.forEach(h -> h.setEndpoint(endpoint));
        endpoint.setHeaders(headers);

        Endpoint saved = endpointService.saveEndpoint(endpoint);

        if (saved.isSecurity()) {
            var jwt =
                    jwtService.createTokenForEndpoint(
                            saved.getProject().getId().toString(),
                            saved.getId().toString(),
                            saved.getExpirationDate());
            if (jwt != null) saved.setJwt(jwt.getToken());
            saved = endpointService.saveEndpoint(saved);
        }

        EndpointResponseDTO response = EndpointMapper.INSTANCE.endpointToResponseDto(saved);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<EndpointResponseDTO> updateEndpoint(
            @PathVariable Long id, @Valid @RequestBody EndpointRequestDTO dto) {

        Endpoint existing =
                endpointService
                        .getEndpointById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint not found"));

        Endpoint updated = EndpointMapper.INSTANCE.dtoToEndpoint(dto);
        updated.setId(id);
        updated.setProject(existing.getProject());

        List<Header> headers = HeaderMapper.INSTANCE.requestDtosToHeaders(dto.getHeaders());
        headers.forEach(h -> h.setEndpoint(updated));
        updated.setHeaders(headers);

        if (updated.isSecurity()) {
            var jwt =
                    jwtService.createTokenForEndpoint(
                            existing.getProject().getId().toString(),
                            existing.getId().toString(),
                            updated.getExpirationDate());
            if (jwt != null) updated.setJwt(jwt.getToken());
        }

        Endpoint saved = endpointService.updateEndpoint(updated, id)
                .orElseThrow(() -> new InternalServerError("Failed to update endpoint"));

        EndpointResponseDTO response =
                EndpointMapper.INSTANCE.endpointToResponseDto(saved);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteEndpoint(@PathVariable Long id) {

        endpointService.getEndpointById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endpoint not found"));

        endpointService.deleteEndpoint(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}