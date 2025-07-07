package org.highfive.backend.global.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.highfive.backend.global.code.ErrorCode;
import org.highfive.backend.global.exception.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.highfive.backend.global.code.GlobalErrorCode.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ErrorResponseDto> handleValidation() {
        final ErrorCode errorCode = INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponseDto(errorCode));
    }
}