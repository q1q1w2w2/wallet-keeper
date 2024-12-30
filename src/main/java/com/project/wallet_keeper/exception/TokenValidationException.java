package com.project.wallet_keeper.exception;

import com.project.wallet_keeper.exception.messages.ErrorMessages;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException() {
        super(INVALID_TOKEN);
    }

    public TokenValidationException(String message) {
        super(message);
    }
}
