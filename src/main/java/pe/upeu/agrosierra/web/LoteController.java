package pe.upeu.agrosierra.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.upeu.agrosierra.servicio.LaborService;
import pe.upeu.agrosierra.servicio.LoteService;
import pe.upeu.agrosierra.web.dto.LoteDto;
import pe.upeu.agrosierra.web.dto.PaginacionResponse;

@RestController
@RequestMapping("/api/v1/lotes")
@RequiredArgsConstructor
public class LoteController {
    private final LoteService loteService;
    private final LaborService laborService;

    @GetMapping
    @PreAuthorize("hasAuthority('LOTE_LEER')")
    public ResponseEntity<PaginacionResponse<LoteDto>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String valle,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(loteService.listarLotes(pagina, tamanio, valle, estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOTE_LEER')")
    public ResponseEntity<LoteDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(loteService.obtenerLote(id));
    }

    @GetMapping("/{id}/labores")
    @PreAuthorize("hasAuthority('LABOR_LEER')")
    public ResponseEntity<pe.upeu.agrosierra.web.dto.PaginacionResponse<pe.upeu.agrosierra.web.dto.LaborDto>> listarLabores(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {
        return ResponseEntity.ok(loteService.listarLabores(id, pagina, tamanio));
    }

    @PostMapping("/{id}/cerrar-campania")
    @PreAuthorize("hasAuthority('CAMPANIA_CERRAR')")
    public ResponseEntity<?> cerrarCampania(@PathVariable Long id) {
        laborService.cerrarCampania(id);
        return ResponseEntity.ok().build();
    }
}
