/* (C)2025 */
package com.o5d.mockify.exception;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {

    @Serial private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
