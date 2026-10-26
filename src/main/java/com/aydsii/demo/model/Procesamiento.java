package com.aydsii.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "procesamiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Procesamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_procesamiento")
    private Integer idProcesamiento;

    @Column(nullable = false)
    private LocalDate fecha;

    /** RN-03: todo procesamiento detalla la cantidad rechazada/desechada. */
    @Column(name = "cantidad_rechazada", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadRechazada;

    /** Usuario con rol OPERADOR que registró el procesamiento. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USUARIO_id_usuario", nullable = false)
    private Usuario usuario;

    /** Cantidad procesada por tipo de material; se guardan en cascada con el procesamiento. */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PROCESAMIENTO_id_procesamiento", nullable = false)
    @Builder.Default
    private List<DetalleProcesamiento> detalleProcesamiento = new ArrayList<>();

    public void addDetalle(DetalleProcesamiento detalle) {
        detalleProcesamiento.add(detalle);
    }

    /** La cantidad procesada total ya no es una columna: es la suma de los detalles. */
    public BigDecimal getCantidadProcesada() {
        return detalleProcesamiento.stream()
                .map(DetalleProcesamiento::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}