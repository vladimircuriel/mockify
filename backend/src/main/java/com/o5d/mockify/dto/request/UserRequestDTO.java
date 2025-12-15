/* (C)2025 */
package com.o5d.mockify.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UserRequestDTO {

    Long id;

    @NotBlank(message = "First name is mandatory") String firstName;

    @NotBlank(message = "Last name is mandatory") String lastName;

    @NotBlank(message = "Username is mandatory") String username;

    @NotBlank(message = "Email is mandatory") @Email String email;

    String password;

    @NotNull(message = "Roles are mandatory") String[] roles;
}
