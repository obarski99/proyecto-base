package com.aydsii.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_material")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_material")
    private Integer idTipoMaterial;

    @Column(nullable = false, length = 100)
    private String nombre;

    /** RN-01: solo el PET es admitido; cualquier otro material se registra como "no PET". */
    @Column(name = "es_pet", nullable = false)
    private Boolean esPet;
}
