package org.highfive.backend.rabbitmq.exception;

public class MessageException extends RuntimeException {
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
