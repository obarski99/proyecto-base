package com.aydsii.demo.exception;

import com.aydsii.demo.dto.ApiResponse;
import lombok.extern.log4j.Log4j2;

import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /** CU-02 / FA-02: período inválido. */
    @ExceptionHandler(PeriodoInvalidoException.class)
    public ResponseEntity<ApiResponse<Void>> handlePeriodoInvalido(PeriodoInvalidoException ex) {
        return error(400, ex.getMessage());
    }

    /** Fecha con formato incorrecto (se espera yyyy-MM-dd). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return error(400, "El valor del parámetro '%s' no es válido. Las fechas deben tener el formato yyyy-MM-dd."
                .formatted(ex.getName()));
    }

    /** CU-02 / FA-04: fallo de conexión o de consulta (DataAccessException cubre la consulta; TransactionException, la conexión). */
    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<ApiResponse<Void>> handleErrorDeDatos(Exception ex) {
        log.error("Error de acceso a datos", ex);
        return error(500, "No se pudo completar la consulta. Intente nuevamente en unos instantes.");
    }

    private ResponseEntity<ApiResponse<Void>> error(int status, String mensaje) {
        return ResponseEntity.status(status).body(ApiResponse.error(status, mensaje));
    }


    /** CU-01 / FA-01: el recolector no tiene asignado el punto de recolección. */
    @ExceptionHandler(AsignacionInvalidaException.class)
    public ResponseEntity<ApiResponse<Void>> handleAsignacionInvalida(AsignacionInvalidaException ex) {
        return error(403, ex.getMessage());
    }

    /** CU-01 / FA-02: cantidad inválida o ningún material informado (validación del service). */
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatosInvalidos(DatosInvalidosException ex) {
        return error(400, ex.getMessage());
    }

    /** CU-01: recolector, punto de recolección o tipo de material inexistente. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return error(404, ex.getMessage());
    }

    /** Bean Validation sobre el body (cantidad <= 0, materiales vacíos, etc.). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return error(400, mensaje);
    }

    /** JSON mal formado (por ejemplo, un id de material que no es numérico). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return error(400, "El cuerpo de la petición no es válido.");
    }

}
