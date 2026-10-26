package com.aydsii.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Materiales recolectados en un punto de recolección")
public class MaterialesDTO {

    @NotEmpty(message = "Debe informar al menos un material")
    @Schema(description = "Cantidad recolectada por tipo de material. La clave es el id del tipo de material "
            + "y el valor es la cantidad recolectada (mayor que 0)",
            example = "{\"1\": 12.5, \"2\": 3}",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<
            @NotNull(message = "El tipo de material es obligatorio") Integer,
            @NotNull(message = "La cantidad es obligatoria")
            @Positive(message = "La cantidad debe ser mayor que 0") Double> materiales;
}