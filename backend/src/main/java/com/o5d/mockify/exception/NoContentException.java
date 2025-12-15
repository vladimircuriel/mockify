/* (C)2025 */
package com.o5d.mockify.exception;

import java.io.Serial;

public class NoContentException extends RuntimeException {
    @Serial private static final long serialVersionUID = 1L;

    public NoContentException(String msg) {
        super(msg);
    }
}
