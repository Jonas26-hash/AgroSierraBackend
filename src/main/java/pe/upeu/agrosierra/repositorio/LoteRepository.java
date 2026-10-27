package pe.upeu.agrosierra.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.agrosierra.dominio.Lote;

public interface LoteRepository extends JpaRepository<Lote, Long> {
    Page<Lote> findByValle(String valle, Pageable pageable);
    Page<Lote> findByCampaniaCerrada(Boolean campaniaCerrada, Pageable pageable);
    Page<Lote> findByValleAndCampaniaCerrada(String valle, Boolean campaniaCerrada, Pageable pageable);
}
