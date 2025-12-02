/* (C)2025 */
package com.o5d.mockify.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Value;

@Value
public class EndpointRequestDTO {

    Long id;

    @NotBlank(message = "Name is mandatory") String name;

    @NotBlank(message = "Description is mandatory") String description;

    @NotBlank(message = "Path is mandatory") String path;

    @NotBlank(message = "Method is mandatory") String method;

    boolean status;

    @Min(value = 0, message = "Delay must be zero or positive") int delay;

    boolean security;

    @NotNull(message = "Expiration date is mandatory") LocalDateTime expirationDate;

    @NotBlank(message = "Encoding is mandatory") String encoding;

    @NotBlank(message = "Response type is mandatory") String responseType;

    @NotBlank(message = "Response status is mandatory") String responseStatus;

    String jwt;

    @NotBlank(message = "Body is mandatory") String body;

    @NotNull(message = "Project ID is mandatory") Long projectId;

    List<HeaderRequestDTO> headers;
}
