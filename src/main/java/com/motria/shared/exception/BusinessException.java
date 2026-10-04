package com.motria.shared.exception;

/** Una regla de negocio impide la operación. Se responde con 400. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
