/* (C)2025 */
package com.o5d.mockify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.o5d.mockify.dto.request.UserRequestDTO;
import com.o5d.mockify.dto.response.UserResponseDTO;
import com.o5d.mockify.model.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserRequestDTO userToDto(User user);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "projects", ignore = true)
    User dtoToUser(UserRequestDTO dto);

    UserResponseDTO userToResponseDto(User user);
}
