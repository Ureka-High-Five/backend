package org.highfive.backend.rabbitmq.exception;

public class MessageDeliveryException extends MessageException{
    public MessageDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
