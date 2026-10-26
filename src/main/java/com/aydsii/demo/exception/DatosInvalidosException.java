package com.aydsii.demo.exception;

/** CU-01 / FA-02: cantidad vacía o menor o igual a 0, o ningún material informado. */
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String message) {
        super(message);
    }
}