package pe.upeu.agrosierra.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoteDto {
    private Long id;
    private String codigo;
    private String nombre;
    private Double hectareas;
    private String cultivo;
    private String valle;
    private Boolean campaniaCerrada;
    private Double dosisMaximaPorHectarea;
}
