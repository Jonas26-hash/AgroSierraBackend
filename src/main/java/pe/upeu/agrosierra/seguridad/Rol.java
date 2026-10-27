package pe.upeu.agrosierra.seguridad;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private RolNombre nombre;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rol_privilegios", joinColumns = @JoinColumn(name = "rol_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "privilegio")
    private Set<Privilegio> privilegios = new HashSet<>();

    public enum RolNombre {
        TECNICO,
        JEFE_CAMPO,
        ADMINISTRADOR
    }
}
