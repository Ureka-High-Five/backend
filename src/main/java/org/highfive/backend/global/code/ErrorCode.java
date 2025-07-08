package org.highfive.backend.global.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    int getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
