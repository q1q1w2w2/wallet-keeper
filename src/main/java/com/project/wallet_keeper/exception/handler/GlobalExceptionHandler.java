package com.project.wallet_keeper.exception.handler;

import com.project.wallet_keeper.dto.response.ApiResponse;
import com.project.wallet_keeper.exception.TokenValidationException;
import com.project.wallet_keeper.exception.UserAlreadyExistException;
import com.project.wallet_keeper.exception.UserNotFoundException;
import com.project.wallet_keeper.exception.VerificationCodeMismatchException;
import com.project.wallet_keeper.exception.messages.ErrorMessages;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception e) {
        return createErrorResponse(e, INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExistException(UserAlreadyExistException e) {
        return createErrorResponse(e, CONFLICT, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException e) {
        return createErrorResponse(e, NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        return createErrorResponse(e, UNAUTHORIZED, INVALID_CREDENTIAL);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenValidationException(TokenValidationException e) {
        return createErrorResponse(e, UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessagingException(MessagingException e) {
        return createErrorResponse(e, INTERNAL_SERVER_ERROR, MAIL_SEND_FAILURE);
    }

    @ExceptionHandler(VerificationCodeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleVerificationCodeMismatchException(VerificationCodeMismatchException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    // Bean Validation 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(e.getMessage());
        return createErrorResponse(e, BAD_REQUEST, message);
    }

    private ResponseEntity<ApiResponse<Object>> createErrorResponse(Exception e, HttpStatus status, String message) {
        log.error("[{}] 발생: {}", e.getClass().getSimpleName(), message);
        ApiResponse<Object> response = ApiResponse.error(status, message);
        return ResponseEntity.status(status).body(response);
    }
}
