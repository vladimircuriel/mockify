/* (C)2025 */
package com.o5d.mockify.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class HeaderRequestDTO {
    Long id;

    @NotBlank(message = "Key is mandatory") String key;

    @NotBlank(message = "Value is mandatory") String value;
}
