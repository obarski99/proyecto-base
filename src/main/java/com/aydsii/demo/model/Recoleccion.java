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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recoleccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recoleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recoleccion")
    private Integer idRecoleccion;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USUARIO_id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PUNTO_RECOLECCION_id_punto_recoleccion", nullable = false)
    private PuntoRecoleccion puntoRecoleccion;

    /** Composición: los detalles se guardan en cascada junto con la recolección. */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RECOLECCION_id_recoleccion", nullable = false)
    @Builder.Default
    private List<DetalleRecoleccion> detalleRecoleccion = new ArrayList<>();

    public void addDetalle(DetalleRecoleccion detalle) {
        detalleRecoleccion.add(detalle);
    }
}