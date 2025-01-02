package com.project.wallet_keeper.exception.user;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
