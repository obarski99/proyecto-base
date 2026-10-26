package com.aydsii.demo.exception;

/** CU-01 / FA-01: el recolector no tiene asignado el punto de recolección (RN-02). */
public class AsignacionInvalidaException extends RuntimeException {

    public AsignacionInvalidaException(String message) {
        super(message);
    }
}