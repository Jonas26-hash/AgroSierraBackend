package pe.upeu.agrosierra.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.upeu.agrosierra.repositorio.UsuarioRepository;
import pe.upeu.agrosierra.seguridad.Usuario;
import pe.upeu.agrosierra.web.dto.UsuarioActualDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    public ResponseEntity<UsuarioActualDto> me(Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();

        Set<String> privilegios = usuario.getRoles().stream()
                .flatMap(r -> r.getPrivilegios().stream())
                .map(p -> p.name())
                .collect(Collectors.toCollection(HashSet::new));

        String rol = usuario.getRoles().stream()
                .findFirst()
                .map(r -> r.getNombre().name())
                .orElse("TECNICO");

        return ResponseEntity.ok(UsuarioActualDto.builder()
                .usuario(usuario.getUsername())
                .rol(rol)
                .privilegios(privilegios)
                .build());
    }
}
