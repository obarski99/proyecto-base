package com.aydsii.demo.controller;

import com.aydsii.demo.dto.MaterialesDTO;
import com.aydsii.demo.dto.RegistrarRecoleccionDTO;
import com.aydsii.demo.service.RecoleccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recolectores")
@RequiredArgsConstructor
@Tag(name = "Recolecciones", description = "Registro de recolecciones de PET realizadas por los recolectores")
public class RecoleccionController {

    private final RecoleccionService recoleccionService;

    @PostMapping("/{idRecolector}/puntos-recoleccion/{idPuntoRecoleccion}/recolecciones")
    @Operation(
        summary = "Registrar una recolección",
        description = "Registra una recolección del recolector indicado en un punto de recolección que tenga asignado. "
                + "La fecha se completa automáticamente con la fecha actual. "
                + "Los materiales que no son PET se registran igual, clasificados como no PET."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Recolección registrada correctamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos incompletos o inválidos (por ejemplo, cantidad menor o igual a 0)"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "El recolector no tiene asignado el punto de recolección"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No existe el recolector, el punto de recolección o algún tipo de material"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error al registrar la información"
        )
    })
    public ResponseEntity<com.aydsii.demo.dto.ApiResponse<RegistrarRecoleccionDTO>> registrarRecoleccion(
            @Parameter(description = "Identificador del recolector (representa al usuario autenticado)", example = "1")
            @PathVariable Integer idRecolector,
            @Valid @RequestBody MaterialesDTO materiales,
            @Parameter(description = "Identificador del punto de recolección atendido", example = "3")
            @PathVariable Integer idPuntoRecoleccion) {

        RegistrarRecoleccionDTO registrada =
                recoleccionService.registrarRecoleccion(idRecolector, materiales, idPuntoRecoleccion);

        return ResponseEntity.status(201).body(
                com.aydsii.demo.dto.ApiResponse.success(201, "Recolección registrada correctamente", registrada));
    }
}