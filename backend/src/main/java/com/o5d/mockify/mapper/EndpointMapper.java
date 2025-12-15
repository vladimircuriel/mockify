/* (C)2025 */
package com.o5d.mockify.mapper;

import com.o5d.mockify.dto.request.EndpointRequestDTO;
import com.o5d.mockify.dto.response.EndpointResponseDTO;
import com.o5d.mockify.model.Endpoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EndpointMapper {

    EndpointMapper INSTANCE = Mappers.getMapper(EndpointMapper.class);

    @Mapping(target = "headers", ignore = true)
    @Mapping(target = "project", ignore = true)
    Endpoint dtoToEndpoint(EndpointRequestDTO dto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "headers", source = "headers")
    EndpointResponseDTO endpointToResponseDto(Endpoint endpoint);
}
