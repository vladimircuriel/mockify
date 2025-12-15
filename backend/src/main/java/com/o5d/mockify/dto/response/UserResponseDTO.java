/* (C)2025 */
package com.o5d.mockify.dto.response;

import com.o5d.mockify.model.Role;
import java.util.Set;
import lombok.Value;

@Value
public class UserResponseDTO {
    Long id;
    String firstName;
    String lastName;
    String username;
    String email;
    Set<Role> roles;
    boolean active;
}
