package com.tecsup.library.service;

/**
 * Excepción de dominio para errores de negocio en la biblioteca.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
