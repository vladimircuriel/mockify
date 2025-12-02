/* (C)2025 */
package com.o5d.mockify.dto.response;

import java.util.List;
import lombok.Value;

@Value
public class ProjectResponseDTO {
    Long id;
    String name;
    String description;
    String tag;
    boolean openAccess;
    boolean status;

    UserResponseDTO owner;
    List<UserResponseDTO> team;
    List<EndpointResponseDTO> endpoints;
}
