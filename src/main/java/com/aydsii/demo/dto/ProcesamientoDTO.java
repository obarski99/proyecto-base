package com.aydsii.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Un procesamiento de PET registrado en la planta")
public record ProcesamientoDTO(
        @Schema(description = "Fecha del procesamiento", example = "2026-10-15") LocalDate fecha,
        @Schema(description = "Tipo de material procesado", example = "PET cristal") String tipoMaterial,
        @Schema(description = "Cantidad procesada con éxito", example = "120.5") Double cantidadProcesada,
        @Schema(description = "Cantidad rechazada/desechada", example = "8.0") Double cantidadRechazada,
        @Schema(description = "Operador que registró el procesamiento", example = "Lucía") String operador) {
}
