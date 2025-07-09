package org.highfive.backend.global.dto;

public record Response<T>(
        int code,
        T content,
        String message
) {
}
