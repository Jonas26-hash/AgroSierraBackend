package pe.upeu.agrosierra.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaborDto {
    private Long id;
    private Long loteId;
    private String tipo;
    private String fecha;
    private String responsable;
    private Double cantidad;
    private String observacion;
}
