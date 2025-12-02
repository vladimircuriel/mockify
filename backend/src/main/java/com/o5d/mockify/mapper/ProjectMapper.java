/* (C)2025 */
package com.o5d.mockify.mapper;

import com.o5d.mockify.dto.request.ProjectRequestDTO;
import com.o5d.mockify.dto.response.ProjectResponseDTO;
import com.o5d.mockify.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {EndpointMapper.class})
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "endpoints", ignore = true)
    @Mapping(target = "status", ignore = true)
    Project dtoToProject(ProjectRequestDTO dto);

    ProjectResponseDTO projectToResponseDto(Project project);
}
