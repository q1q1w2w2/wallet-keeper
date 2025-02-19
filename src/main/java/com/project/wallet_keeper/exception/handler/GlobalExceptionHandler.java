package com.project.wallet_keeper.exception.handler;

import com.project.wallet_keeper.util.common.ApiResponse;
import com.project.wallet_keeper.exception.auth.OAuthUserException;
import com.project.wallet_keeper.exception.auth.TokenValidationException;
import com.project.wallet_keeper.exception.auth.VerificationCodeMismatchException;
import com.project.wallet_keeper.exception.budget.BudgetNotFoundException;
import com.project.wallet_keeper.exception.scheduler.SchedulerExecutionException;
import com.project.wallet_keeper.exception.transaction.CategoryAlreadyExistException;
import com.project.wallet_keeper.exception.transaction.InvalidTransactionOwnerException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.exception.transaction.TransactionNotFoundException;
import com.project.wallet_keeper.exception.user.UserAlreadyExistException;
import com.project.wallet_keeper.exception.user.UserNotActiveException;
import com.project.wallet_keeper.exception.user.UserNotFoundException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;
import static com.project.wallet_keeper.util.common.ApiResponseUtil.*;
import static org.springframework.http.HttpStatus.*;

//@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception e) {
        return createErrorResponse(e, INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExistException(UserAlreadyExistException e) {
        return createErrorResponse(e, CONFLICT, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException e) {
        return createErrorResponse(e, NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotActiveException(UserNotActiveException e) {
        return createErrorResponse(e, UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        return createErrorResponse(e, UNAUTHORIZED, INVALID_CREDENTIAL);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenValidationException(TokenValidationException e) {
        return createErrorResponse(e, UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(OAuthUserException.class)
    public ResponseEntity<ApiResponse<Object>> handleOAuthUserException(OAuthUserException e) {
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

    @ExceptionHandler(TransactionCategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleTransactionCategoryNotFoundException(TransactionCategoryNotFoundException e) {
        return createErrorResponse(e, NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleTransactionNotFoundException(TransactionNotFoundException e) {
        return createErrorResponse(e, NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidTransactionOwnerException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTransactionOwnerException(InvalidTransactionOwnerException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleBudgetNotFoundException(BudgetNotFoundException e) {
        return createErrorResponse(e, NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryAlreadyExistException(CategoryAlreadyExistException e) {
        return createErrorResponse(e, CONFLICT, e.getMessage());
    }

    @ExceptionHandler(SchedulerExecutionException.class)
    public ResponseEntity<ApiResponse<Object>> handleSchedulerExecutionException(SchedulerExecutionException e) {
        return createErrorResponse(e, INTERNAL_SERVER_ERROR, e.getMessage());
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
}
