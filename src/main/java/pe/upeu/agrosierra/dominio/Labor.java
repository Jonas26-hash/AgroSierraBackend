package pe.upeu.agrosierra.dominio;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "labores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Labor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Enumerated(EnumType.STRING)
    private TipoLabor tipo;

    private LocalDate fecha;

    private String responsable;

    private Double cantidad;

    @Column(length = 1000)
    private String observacion;
}
