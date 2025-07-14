package org.highfive.backend.global.dto;

import org.highfive.backend.global.code.SuccessCode;

public record Response<T>(
        int code,
        T content,
        String message
) {

    public static <T> Response<T> ok(final T content) {
        return new Response<>(SuccessCode.OK.getCode(), content, SuccessCode.OK.getMessage());
    }
}
