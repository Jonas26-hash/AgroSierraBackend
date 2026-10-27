package pe.upeu.agrosierra.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upeu.agrosierra.config.ManejadorDeErrores;
import pe.upeu.agrosierra.dominio.Labor;
import pe.upeu.agrosierra.dominio.Lote;
import pe.upeu.agrosierra.dominio.TipoLabor;
import pe.upeu.agrosierra.repositorio.LaborRepository;
import pe.upeu.agrosierra.repositorio.LoteRepository;
import pe.upeu.agrosierra.web.dto.LaborDto;
import pe.upeu.agrosierra.web.dto.LaborRequest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LaborService {
    private final LaborRepository laborRepository;
    private final LoteRepository loteRepository;

    @Transactional
    public LaborDto crear(LaborRequest request, String username) {
        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new ManejadorDeErrores.ReglaNegocioException("Lote no encontrado", HttpStatus.NOT_FOUND));

        // RN-01
        if (Boolean.TRUE.equals(lote.getCampaniaCerrada())) {
            throw new ManejadorDeErrores.ReglaNegocioException("No se puede registrar labores sobre un lote con campaña cerrada", HttpStatus.CONFLICT);
        }

        TipoLabor tipoLabor = TipoLabor.valueOf(request.getTipo());

        // RN-04
        if (tipoLabor == TipoLabor.FUMIGACION) {
            double dosisMax = lote.getDosisMaximaPorHectarea() != null ? lote.getDosisMaximaPorHectarea() : 0.0;
            double hectareas = lote.getHectareas() != null ? lote.getHectareas() : 0.0;
            double maxPermitido = dosisMax * hectareas;
            if (request.getCantidad() != null && request.getCantidad() > maxPermitido) {
                Map<String, String> errors = new HashMap<>();
                errors.put("cantidad", "La cantidad aplicada supera la dosis máxima permitida");
                throw new ManejadorDeErrores.ReglaNegocioException("Datos invalidos", HttpStatus.BAD_REQUEST, errors);
            }
        }

        Labor labor = Labor.builder()
                .lote(lote)
                .tipo(tipoLabor)
                .fecha(LocalDate.parse(request.getFecha()))
                .responsable(username != null ? username : request.getResponsable())
                .cantidad(request.getCantidad())
                .observacion(request.getObservacion())
                .build();
        labor = laborRepository.save(labor);
        return toDto(labor);
    }

    @Transactional
    public LaborDto actualizar(Long id, LaborRequest request, String username) {
        Labor labor = laborRepository.findById(id)
                .orElseThrow(() -> new ManejadorDeErrores.ReglaNegocioException("Labor no encontrada", HttpStatus.NOT_FOUND));
        Lote lote = labor.getLote();

        // RN-01
        if (Boolean.TRUE.equals(lote.getCampaniaCerrada())) {
            throw new ManejadorDeErrores.ReglaNegocioException("No se puede corregir labores sobre un lote con campaña cerrada", HttpStatus.CONFLICT);
        }

        // RN-02 - técnico solo puede corregir sus propias labores y dentro de 24 horas
        // Verificamos por responsable si es técnico; el control por privilegio se hace en seguridad
        if (labor.getResponsable() != null && username != null && !labor.getResponsable().equals(username)) {
            // Si es distinto usuario, podría ser técnico intentando editar ajena - pero JEFE_CAMPO tiene LABOR_EDITAR para todas
            // La autorización por privilegio ya la maneja @PreAuthorize, pero RN-02 dice "Un técnico solo puede corregir..."
            // Asumimos que si llega aquí con privilegio LABOR_EDITAR global (JEFE_CAMPO) puede hacerlo; si solo es técnico (propias) ya filtrado
            // Para cumplir estrictamente: si responsable != username y no es jefe/admin con editar todas, bloquear
        }
        // RN-02 más estricto: dentro de 24 horas
        if (labor.getFecha() != null) {
            long horas = ChronoUnit.HOURS.between(labor.getFecha().atStartOfDay(), LocalDate.now().atStartOfDay());
            if (horas > 24 && labor.getResponsable() != null && username != null && labor.getResponsable().equals(username)) {
                throw new ManejadorDeErrores.ReglaNegocioException("No autorizado", HttpStatus.FORBIDDEN);
            }
            if (labor.getResponsable() != null && username != null && !labor.getResponsable().equals(username)) {
                // Si es otra persona, verificar - técnico no puede; pero jefe puede (tiene privilegio todas). 
                // Como la regla dice "Un técnico solo puede corregir las labores que él mismo registró...", 
                // y el jefe puede corregir todas según matriz
                throw new ManejadorDeErrores.ReglaNegocioException("No autorizado", HttpStatus.FORBIDDEN);
            }
        }

        TipoLabor tipoLabor = request.getTipo() != null ? TipoLabor.valueOf(request.getTipo()) : labor.getTipo();

        // RN-04
        if (tipoLabor == TipoLabor.FUMIGACION) {
            double dosisMax = lote.getDosisMaximaPorHectarea() != null ? lote.getDosisMaximaPorHectarea() : 0.0;
            double hectareas = lote.getHectareas() != null ? lote.getHectareas() : 0.0;
            double maxPermitido = dosisMax * hectareas;
            if (request.getCantidad() != null && request.getCantidad() > maxPermitido) {
                Map<String, String> errors = new HashMap<>();
                errors.put("cantidad", "La cantidad aplicada supera la dosis máxima permitida");
                throw new ManejadorDeErrores.ReglaNegocioException("Datos invalidos", HttpStatus.BAD_REQUEST, errors);
            }
        }

        labor.setTipo(tipoLabor);
        if (request.getFecha() != null) labor.setFecha(LocalDate.parse(request.getFecha()));
        if (request.getCantidad() != null) labor.setCantidad(request.getCantidad());
        labor.setObservacion(request.getObservacion());
        labor = laborRepository.save(labor);
        return toDto(labor);
    }

    @Transactional
    public void eliminar(Long id) {
        Labor labor = laborRepository.findById(id)
                .orElseThrow(() -> new ManejadorDeErrores.ReglaNegocioException("Labor no encontrada", HttpStatus.NOT_FOUND));
        laborRepository.delete(labor);
    }

    @Transactional
    public void cerrarCampania(Long loteId) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new ManejadorDeErrores.ReglaNegocioException("Lote no encontrado", HttpStatus.NOT_FOUND));
        // RN-03
        long countCosecha = laborRepository.countByLoteIdAndTipo(loteId, TipoLabor.COSECHA);
        if (countCosecha == 0) {
            throw new ManejadorDeErrores.ReglaNegocioException("No se puede cerrar la campaña sin al menos una labor de tipo Cosecha", HttpStatus.CONFLICT);
        }
        lote.setCampaniaCerrada(true);
        loteRepository.save(lote);
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
