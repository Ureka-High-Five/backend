package org.highfive.backend.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response<T>(
        int code,
        T content,
        String message
) {
}
