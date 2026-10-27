package pe.upeu.agrosierra.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.agrosierra.dominio.Labor;
import java.util.List;

public interface LaborRepository extends JpaRepository<Labor, Long> {
    Page<Labor> findByLoteIdOrderByFechaDesc(Long loteId, Pageable pageable);
    List<Labor> findByLoteId(Long loteId);
    long countByLoteIdAndTipo(Long loteId, pe.upeu.agrosierra.dominio.TipoLabor tipo);
}
