/* (C)2025 */
package com.o5d.mockify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ProjectRequestDTO {
    Long id;

    @NotBlank(message = "Name is mandatory") String name;

    @NotBlank(message = "Description is mandatory") String description;

    @NotBlank(message = "Tag is mandatory") String tag;

    boolean openAccess;

    @NotNull(message = "Owner ID is mandatory") Long ownerId;
}
