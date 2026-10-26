package com.aydsii.demo.exception;

/** CU-02 / FA-02: la fecha desde es posterior a la fecha hasta. */
public class PeriodoInvalidoException extends RuntimeException {

    public PeriodoInvalidoException(String message) {
        super(message);
    }
}
