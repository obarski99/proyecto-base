package com.aydsii.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Historial de procesamientos del período consultado")
public record HistorialResponseDTO(
        @Schema(description = "Inicio del período consultado (null si no se indicó)", example = "2026-10-01",
                nullable = true) LocalDate fechaDesde,
        @Schema(description = "Fin del período consultado (null si no se indicó)", example = "2026-10-31",
                nullable = true) LocalDate fechaHasta,
        @Schema(description = "Procesamientos encontrados, del más reciente al más antiguo")
        List<ProcesamientoDTO> procesamientos) {
}
