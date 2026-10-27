package pe.upeu.agrosierra.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upeu.agrosierra.config.ManejadorDeErrores;
import pe.upeu.agrosierra.dominio.Labor;
import pe.upeu.agrosierra.dominio.Lote;
import pe.upeu.agrosierra.dominio.TipoLabor;
import pe.upeu.agrosierra.repositorio.LaborRepository;
import pe.upeu.agrosierra.repositorio.LoteRepository;
import pe.upeu.agrosierra.web.dto.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {
    private final LoteRepository loteRepository;
    private final LaborRepository laborRepository;

    public PaginacionResponse<LoteDto> listarLotes(int pagina, int tamanio, String valle, String estado) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<Lote> page;
        if (valle != null && estado != null) {
            Boolean campaniaCerrada = "cerrada".equalsIgnoreCase(estado) || "true".equalsIgnoreCase(estado);
            page = loteRepository.findByValleAndCampaniaCerrada(valle, campaniaCerrada, pageable);
        } else if (valle != null) {
            page = loteRepository.findByValle(valle, pageable);
        } else if (estado != null) {
            Boolean campaniaCerrada = "cerrada".equalsIgnoreCase(estado) || "true".equalsIgnoreCase(estado);
            page = loteRepository.findByCampaniaCerrada(campaniaCerrada, pageable);
        } else {
            page = loteRepository.findAll(pageable);
        }
        List<LoteDto> contenido = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return PaginacionResponse.<LoteDto>builder()
                .contenido(contenido)
                .pagina(page.getNumber())
                .tamanio(page.getSize())
                .totalElementos(page.getTotalElements())
                .totalPaginas(page.getTotalPages())
                .ultima(page.isLast())
                .build();
    }

    public LoteDto obtenerLote(Long id) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ManejadorDeErrores.ReglaNegocioException("Lote no encontrado", org.springframework.http.HttpStatus.NOT_FOUND));
        return toDto(lote);
    }

    public PaginacionResponse<LaborDto> listarLabores(Long loteId, int pagina, int tamanio) {
        if (!loteRepository.existsById(loteId)) {
            throw new ManejadorDeErrores.ReglaNegocioException("Lote no encontrado", org.springframework.http.HttpStatus.NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<Labor> page = laborRepository.findByLoteIdOrderByFechaDesc(loteId, pageable);
        List<LaborDto> contenido = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return PaginacionResponse.<LaborDto>builder()
                .contenido(contenido)
                .pagina(page.getNumber())
                .tamanio(page.getSize())
                .totalElementos(page.getTotalElements())
                .totalPaginas(page.getTotalPages())
                .ultima(page.isLast())
                .build();
    }

    private LoteDto toDto(Lote lote) {
        return LoteDto.builder()
                .id(lote.getId())
                .codigo(lote.getCodigo())
                .nombre(lote.getNombre())
                .hectareas(lote.getHectareas())
                .cultivo(lote.getCultivo())
                .valle(lote.getValle())
                .campaniaCerrada(lote.getCampaniaCerrada())
                .dosisMaximaPorHectarea(lote.getDosisMaximaPorHectarea())
                .build();
    }

    private LaborDto toDto(Labor labor) {
        return LaborDto.builder()
                .id(labor.getId())
                .loteId(labor.getLote().getId())
                .tipo(labor.getTipo().name())
                .fecha(labor.getFecha() != null ? labor.getFecha().toString() : null)
                .responsable(labor.getResponsable())
                .cantidad(labor.getCantidad())
                .observacion(labor.getObservacion())
                .build();
    }
}
