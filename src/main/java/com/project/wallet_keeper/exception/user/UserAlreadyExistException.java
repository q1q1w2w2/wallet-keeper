package com.project.wallet_keeper.exception.user;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException() {
        super(USER_ALREADY_EXIST);
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
