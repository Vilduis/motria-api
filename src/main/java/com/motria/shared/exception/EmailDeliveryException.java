package com.motria.shared.exception;

/** El proveedor de correo no pudo enviar el mensaje. Se responde con 503. */
public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
