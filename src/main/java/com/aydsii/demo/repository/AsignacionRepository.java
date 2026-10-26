package com.aydsii.demo.repository;

import com.aydsii.demo.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {

    /**
     * RN-02: el punto de recolección está asignado al recolector y la asignación
     * está vigente hoy (fechaHasta nula significa sin fecha de fin).
     */
    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM Asignacion a
            WHERE a.usuario.idUsuario = :idUsuario
              AND a.puntoRecoleccion.idPuntoRecoleccion = :idPuntoRecoleccion
              AND a.fechaDesde <= CURRENT_DATE
              AND (a.fechaHasta IS NULL OR a.fechaHasta >= CURRENT_DATE)
            """)
    boolean asignacionValida(@Param("idUsuario") Integer idUsuario,
                             @Param("idPuntoRecoleccion") Integer idPuntoRecoleccion);
}