package com.aydsii.demo.exception;

/** CU-01: el recolector, el punto de recolección o el tipo de material no existen. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}