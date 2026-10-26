package com.aydsii.demo.controller;

import com.aydsii.demo.dto.ApiResponse;
import com.aydsii.demo.dto.HistorialResponseDTO;
import com.aydsii.demo.service.ProcesamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/procesamientos")
@RequiredArgsConstructor
@Tag(name = "Procesamientos", description = "Procesamiento de PET en la planta")
public class ProcesamientoController {

    static final String MSG_OK = "Historial de procesamientos obtenido correctamente.";
    static final String MSG_SIN_RESULTADOS =
            "No hay procesamientos registrados en el período indicado. Pruebe con otro período.";

    private final ProcesamientoService procesamientoService;

    // Las anotaciones de Swagger usan el nombre completo porque "ApiResponse" también es el nombre
    // del envoltorio propio (com.aydsii.demo.dto.ApiResponse).
    @GetMapping("/historial")
    @Operation(
            summary = "Consultar historial de procesamientos (CU-02)",
            description = """
                    Devuelve los procesamientos de PET registrados en la planta para el período indicado, \
                    del más reciente al más antiguo. Ambas fechas son inclusivas y opcionales: si no se \
                    indica ninguna se consulta todo el historial.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Consulta exitosa. Si no hay procesamientos en el período, "
                            + "'procesamientos' viene vacío y el mensaje lo informa (FA-03)."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Período inválido: fecha desde posterior a fecha hasta, "
                            + "o formato de fecha incorrecto (FA-02).",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
                    description = "Fallo de conexión o de consulta; se puede reintentar (FA-04).",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<HistorialResponseDTO>> consultarHistorial(
            @Parameter(description = "Fecha desde (inclusive), formato yyyy-MM-dd", example = "2026-10-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,

            @Parameter(description = "Fecha hasta (inclusive), formato yyyy-MM-dd", example = "2026-10-31")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {

        HistorialResponseDTO historial = procesamientoService.consultarHistorial(fechaDesde, fechaHasta);

        String mensaje = historial.procesamientos().isEmpty() ? MSG_SIN_RESULTADOS : MSG_OK;
        ApiResponse<HistorialResponseDTO> apiResponse = ApiResponse.success(200, mensaje, historial);

        return ResponseEntity.status(200).body(apiResponse);
    }
}
