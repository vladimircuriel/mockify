package com.o5d.mockify.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginRequestDTO {

    @NotBlank(message = "Username is mandatory")
    String username;

    @NotBlank(message = "Password is mandatory")
    String password;
}