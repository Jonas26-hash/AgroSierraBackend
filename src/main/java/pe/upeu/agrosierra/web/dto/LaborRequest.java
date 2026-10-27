package pe.upeu.agrosierra.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LaborRequest {
    @NotNull
    private Long loteId;
    @NotBlank
    private String tipo;
    @NotBlank
    private String fecha;
    private String responsable;
    @NotNull
    private Double cantidad;
    private String observacion;
}
