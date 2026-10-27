package pe.upeu.agrosierra.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioActualDto {
    private String usuario;
    private String rol;
    private Set<String> privilegios;
}
