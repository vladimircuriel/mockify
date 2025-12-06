/* (C)2025 */
package com.o5d.mockify.mapper;

import com.o5d.mockify.dto.request.HeaderRequestDTO;
import com.o5d.mockify.dto.response.HeaderResponseDTO;
import com.o5d.mockify.model.Header;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeaderMapper {
    HeaderMapper INSTANCE = Mappers.getMapper(HeaderMapper.class);

    @Mapping(target = "endpoint", ignore = true)
    Header dtoToHeader(HeaderRequestDTO dto);

    HeaderRequestDTO headerToDto(Header header);

    List<Header> requestDtosToHeaders(List<HeaderRequestDTO> dtos);

    List<HeaderResponseDTO> headersToResponseDtos(List<Header> headers);
}
