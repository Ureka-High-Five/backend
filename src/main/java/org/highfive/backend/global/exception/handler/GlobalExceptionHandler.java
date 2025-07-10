package org.highfive.backend.global.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.global.code.ErrorCode;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.global.exception.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(final BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        log.error("{}", errorCode.getMessage(), e);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponseDto(errorCode));
    }
}
