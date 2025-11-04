package com.ecommerce.ecommerce.core.exception;


import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(
          ResourceNotFoundException ex, WebRequest request) {
    logger.error("Resource not found: {}", ex.getMessage());

    return new ResponseEntity<>(
            ApiResponse.error(
                    ex.getErrorCode().getStatus().value(),
                    ex.getErrorCode().getMessage(),
                    request.getDescription(false).replace("uri=", ""),
                    null
            ),
            ex.getErrorCode().getStatus()
    );
  }

  @ExceptionHandler(PaymentException.class)
  public ResponseEntity<ApiResponse<?>> handlePaymentException(
          PaymentException ex, WebRequest request) {
    logger.error("Payment error: {}", ex.getMessage());

    return new ResponseEntity<>(
            ApiResponse.error(
                    ex.getErrorCode().getStatus().value(),
                    ex.getErrorCode().getMessage(),
                    request.getDescription(false).replace("uri=", ""),
                    null
            ),
            ex.getErrorCode().getStatus()
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex, WebRequest request) {
    logger.error("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

    return new ResponseEntity<>(
            ApiResponse.error(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Failed",
                    request.getDescription(false).replace("uri=", ""),
                    errors
            ),
            HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResponse<?>> handleBindException(
          BindException ex, WebRequest request) {
    logger.error("Bind error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

    return new ResponseEntity<>(
            ApiResponse.error(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Failed",
                    request.getDescription(false).replace("uri=", ""),
                    errors
            ),
            HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(
          AccessDeniedException ex, WebRequest request) {
    logger.error("Access denied: {}", ex.getMessage());

    return new ResponseEntity<>(
            ApiResponse.error(
                    HttpStatus.FORBIDDEN.value(),
                    "Access Denied",
                    request.getDescription(false).replace("uri=", ""),
                    null
            ),
            HttpStatus.FORBIDDEN
    );
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<?>> handleBusinessException(
          BusinessException ex, WebRequest request) {
    logger.error("Business error: {}", ex.getMessage());

    return new ResponseEntity<>(
            ApiResponse.error(
                    ex.getErrorCode().getStatus().value(),
                    ex.getErrorCode().getMessage(),
                    request.getDescription(false).replace("uri=", ""),
                    null
            ),
            ex.getErrorCode().getStatus()
    );
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiResponse<?>> handleValidationException(
          ValidationException ex, WebRequest request) {
    logger.error("Validation error: {}", ex.getMessage());

    return new ResponseEntity<>(
            ApiResponse.error(
                    ex.getErrorCode().getStatus().value(),
                    ex.getErrorCode().getMessage(),
                    request.getDescription(false).replace("uri=", ""),
                    ex.getFieldErrors()
            ),
            ex.getErrorCode().getStatus()
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleGlobalException(
          Exception ex, WebRequest request) {
    logger.error("Unexpected error: ", ex);

    return new ResponseEntity<>(
            ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    request.getDescription(false).replace("uri=", ""),
                    null
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
    );
  }

}