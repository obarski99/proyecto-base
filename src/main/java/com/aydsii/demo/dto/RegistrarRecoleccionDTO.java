package com.aydsii.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de la recolección registrada")
public class RegistrarRecoleccionDTO {

    @Schema(description = "Identificador de la recolección registrada", example = "15")
    private Integer idRecoleccion;

    @Schema(description = "Cantidad registrada por tipo de material. La clave es el id del tipo de material",
            example = "{\"1\": 12.5, \"2\": 3}")
    private Map<Integer, Double> materiales;

    @Schema(description = "Identificador del recolector que realizó el registro", example = "1")
    private Integer idUsuario;

    @Schema(description = "Fecha en la que se registró la recolección", example = "2026-10-26")
    private LocalDate fecha;

    @Schema(description = "Identificador del punto de recolección atendido", example = "3")
    private Integer idPuntoRecoleccion;
}