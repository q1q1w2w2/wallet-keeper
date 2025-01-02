package com.project.wallet_keeper.exception.auth;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class VerificationCodeMismatchException extends RuntimeException {

    public VerificationCodeMismatchException() {
        super(CODE_MISMATCH);
    }

    public VerificationCodeMismatchException(String message) {
        super(message);
    }
}
