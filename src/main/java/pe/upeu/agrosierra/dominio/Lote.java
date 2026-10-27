package pe.upeu.agrosierra.dominio;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nombre;
    private Double hectareas;
    private String cultivo;
    private String valle;
    
    @Column(name = "campania_cerrada")
    private Boolean campaniaCerrada = false;
    
    @Column(name = "dosis_maxima_por_hectarea")
    private Double dosisMaximaPorHectarea;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Labor> labores = new ArrayList<>();
}
