package com.aydsii.demo.service;

import com.aydsii.demo.dto.MaterialesDTO;
import com.aydsii.demo.dto.RegistrarRecoleccionDTO;
import com.aydsii.demo.exception.AsignacionInvalidaException;
import com.aydsii.demo.exception.DatosInvalidosException;
import com.aydsii.demo.exception.RecursoNoEncontradoException;
import com.aydsii.demo.model.DetalleRecoleccion;
import com.aydsii.demo.model.PuntoRecoleccion;
import com.aydsii.demo.model.Recoleccion;
import com.aydsii.demo.model.TipoMaterial;
import com.aydsii.demo.model.Usuario;
import com.aydsii.demo.repository.AsignacionRepository;
import com.aydsii.demo.repository.PuntoRecoleccionRepository;
import com.aydsii.demo.repository.RecoleccionRepository;
import com.aydsii.demo.repository.TipoMaterialRepository;
import com.aydsii.demo.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecoleccionService {

    // Ajustar al valor real que se guarda en la columna usuario.rol
    private static final String ROL_RECOLECTOR = "RECOLECTOR";

    private final UsuarioRepository usuarioRepository;
    private final PuntoRecoleccionRepository puntoRecoleccionRepository;
    private final AsignacionRepository asignacionRepository;
    private final TipoMaterialRepository tipoMaterialRepository;
    private final RecoleccionRepository recoleccionRepository;

    @Transactional
    public RegistrarRecoleccionDTO registrarRecoleccion(Integer idRecolector,
                                                        MaterialesDTO materiales,
                                                        Integer idPuntoRecoleccion) {

        // Validación defensiva: no se confía en que el frontend mande datos prolijos
        if (materiales == null || materiales.getMateriales() == null || materiales.getMateriales().isEmpty()) {
            throw new DatosInvalidosException("Debe informar al menos un material");
        }

        Usuario recolector = usuarioRepository.findById(idRecolector)
                .filter(usuario -> ROL_RECOLECTOR.equalsIgnoreCase(usuario.getRol()))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un recolector con id " + idRecolector));

        PuntoRecoleccion punto = puntoRecoleccionRepository.findById(idPuntoRecoleccion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el punto de recolección con id " + idPuntoRecoleccion));

        if (!asignacionRepository.asignacionValida(idRecolector, idPuntoRecoleccion)) {
            throw new AsignacionInvalidaException(
                    "El recolector no tiene asignado el punto de recolección " + idPuntoRecoleccion);
        }

        Recoleccion recoleccion = Recoleccion.builder()
                .fecha(LocalDate.now())
                .usuario(recolector)
                .puntoRecoleccion(punto)
                .build();

        for (Map.Entry<Integer, Double> item : materiales.getMateriales().entrySet()) {
            Integer idTipoMaterial = item.getKey();
            Double cantidad = item.getValue();

            if (cantidad == null || cantidad <= 0) {
                throw new DatosInvalidosException("La cantidad debe ser mayor que 0");
            }

            TipoMaterial tipoMaterial = tipoMaterialRepository.findById(idTipoMaterial)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe el tipo de material con id " + idTipoMaterial));

            // RN-01: si el material no es PET igual se registra; queda clasificado por tipoMaterial.esPet
            recoleccion.addDetalle(DetalleRecoleccion.builder()
                    .tipoMaterial(tipoMaterial)
                    .cantidad(cantidad)
                    .build());
        }

        // Un solo save: los detalles se guardan en cascada junto con la recolección
        Recoleccion guardada = recoleccionRepository.save(recoleccion);

        return armarRespuesta(guardada);
    }

    private RegistrarRecoleccionDTO armarRespuesta(Recoleccion recoleccion) {
        Map<Integer, Double> materiales = new LinkedHashMap<>();
        recoleccion.getDetalleRecoleccion().forEach(detalle ->
                materiales.put(detalle.getTipoMaterial().getIdTipoMaterial(), detalle.getCantidad()));

        return RegistrarRecoleccionDTO.builder()
                .idRecoleccion(recoleccion.getIdRecoleccion())
                .materiales(materiales)
                .idUsuario(recoleccion.getUsuario().getIdUsuario())
                .fecha(recoleccion.getFecha())
                .idPuntoRecoleccion(recoleccion.getPuntoRecoleccion().getIdPuntoRecoleccion())
                .build();
    }
}