package com.aydsii.demo.service;

import com.aydsii.demo.dto.HistorialResponseDTO;
import com.aydsii.demo.dto.ProcesamientoDTO;
import com.aydsii.demo.exception.PeriodoInvalidoException;
import com.aydsii.demo.model.Procesamiento;
import com.aydsii.demo.repository.ProcesamientoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProcesamientoService {

    private final ProcesamientoRepository procesamientoRepository;

    /**
     * CU-02: consulta el historial de procesamientos del período indicado.
     * Si no se indica ningún extremo, se considera todo el historial (FA-01).
     */
    @Transactional(readOnly = true)
    public HistorialResponseDTO consultarHistorial(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.debug("Consultando historial de procesamientos desde={} hasta={}", fechaDesde, fechaHasta);

        validarPeriodo(fechaDesde, fechaHasta);

        List<ProcesamientoDTO> procesamientos = procesamientoRepository
                .buscarProcesamientosConFiltros(fechaDesde, fechaHasta)
                .stream()
                .map(this::toDTO)
                .toList();

        log.debug("Procesamientos encontrados: {}", procesamientos.size());
        return new HistorialResponseDTO(fechaDesde, fechaHasta, procesamientos);
    }

    /** FA-02: la fecha desde no puede ser posterior a la fecha hasta. */
    void validarPeriodo(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new PeriodoInvalidoException(
                    "La fecha desde (%s) no puede ser posterior a la fecha hasta (%s)."
                            .formatted(fechaDesde, fechaHasta));
        }
    }

    private ProcesamientoDTO toDTO(Procesamiento p) {
    String tipos = p.getDetalleProcesamiento().stream()
            .map(d -> d.getTipoMaterial().getNombre())
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));

    return new ProcesamientoDTO(
            p.getFecha(),
            tipos,
            p.getCantidadProcesada().doubleValue(),
            p.getCantidadRechazada().doubleValue(),
            p.getUsuario().getNombre());
}
}
