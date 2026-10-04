package com.motria.shared.exception;

/** El recurso no existe o no pertenece al taller del usuario. Se responde con 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /** Ejemplo: {@code ResourceNotFoundException.of("el cliente", 5)} → "No se encontró el cliente con id 5". */
    public static ResourceNotFoundException of(String resource, Long id) {
        return new ResourceNotFoundException("No se encontró " + resource + " con id " + id);
    }
}
