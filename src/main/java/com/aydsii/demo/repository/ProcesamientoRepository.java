package com.aydsii.demo.repository;

import com.aydsii.demo.model.Procesamiento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcesamientoRepository extends JpaRepository<Procesamiento, Integer> {

    /**
     * Procesamientos del período indicado (ambos extremos inclusivos), con su operador y su
     * tipo de material ya cargados. Un extremo en {@code null} significa "sin límite" (FA-01 del CU-02).
     * Orden: más recientes primero.
     */
    @Query("""
            SELECT DISTINCT p FROM Procesamiento p
            JOIN FETCH p.usuario
            LEFT JOIN FETCH p.detalleProcesamiento d
            LEFT JOIN FETCH d.tipoMaterial
            WHERE (:fechaDesde IS NULL OR p.fecha >= :fechaDesde)
              AND (:fechaHasta IS NULL OR p.fecha <= :fechaHasta)
            ORDER BY p.fecha DESC, p.idProcesamiento DESC
            """)
    List<Procesamiento> buscarProcesamientosConFiltros(@Param("fechaDesde") LocalDate fechaDesde,
                                                       @Param("fechaHasta") LocalDate fechaHasta);
}
