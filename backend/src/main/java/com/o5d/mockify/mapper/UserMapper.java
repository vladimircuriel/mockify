/* (C)2025 */
package com.o5d.mockify.mapper;

import com.o5d.mockify.dto.request.UserRequestDTO;
import com.o5d.mockify.dto.response.UserResponseDTO;
import com.o5d.mockify.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", ignore = true)
    UserRequestDTO userToDto(User user);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User dtoToUser(UserRequestDTO dto);

    UserResponseDTO userToResponseDto(User user);

    List<UserResponseDTO> usersToResponseDtos(List<User> users);
}
