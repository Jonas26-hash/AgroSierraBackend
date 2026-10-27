package pe.upeu.agrosierra.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.upeu.agrosierra.servicio.LaborService;
import pe.upeu.agrosierra.web.dto.LaborDto;
import pe.upeu.agrosierra.web.dto.LaborRequest;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/labores")
@RequiredArgsConstructor
public class LaborController {
    private final LaborService laborService;

    @PostMapping
    @PreAuthorize("hasAuthority('LABOR_CREAR')")
    public ResponseEntity<LaborDto> crear(@Valid @RequestBody LaborRequest request, Principal principal) {
        LaborDto dto = laborService.crear(request, principal != null ? principal.getName() : null);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LABOR_EDITAR')")
    public ResponseEntity<LaborDto> actualizar(@PathVariable Long id, @Valid @RequestBody LaborRequest request, Principal principal) {
        LaborDto dto = laborService.actualizar(id, request, principal != null ? principal.getName() : null);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LABOR_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        laborService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
