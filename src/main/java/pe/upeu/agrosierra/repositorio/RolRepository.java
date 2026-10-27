package pe.upeu.agrosierra.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.agrosierra.seguridad.Rol;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(Rol.RolNombre nombre);
}
