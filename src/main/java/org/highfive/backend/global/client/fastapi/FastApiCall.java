package org.highfive.backend.global.client.fastapi;

import org.springframework.web.client.RestClientException;

@FunctionalInterface
public interface FastApiCall<T> {
    T call() throws RestClientException;
}
