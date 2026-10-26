package com.aydsii.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Envoltorio estándar de las respuestas de la API. */
@Schema(description = "Respuesta estándar de la API")
public record ApiResponse<T>(
        @Schema(description = "Código HTTP de la respuesta", example = "200") int status,
        @Schema(description = "Mensaje para el usuario") String message,
        @Schema(description = "Datos de la respuesta (null en caso de error)") T data) {

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
