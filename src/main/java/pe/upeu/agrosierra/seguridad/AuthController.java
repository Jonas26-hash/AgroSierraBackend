package pe.upeu.agrosierra.seguridad;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import pe.upeu.agrosierra.config.JwtService;
import pe.upeu.agrosierra.repositorio.UsuarioRepository;
import pe.upeu.agrosierra.web.dto.LoginRequest;
import pe.upeu.agrosierra.web.dto.LoginResponse;
import pe.upeu.agrosierra.web.dto.UsuarioActualDto;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();

        Set<String> privilegios = usuario.getRoles().stream()
                .flatMap(r -> r.getPrivilegios().stream())
                .map(p -> p.name())
                .collect(Collectors.toCollection(HashSet::new));

        String rol = usuario.getRoles().stream()
                .findFirst()
                .map(r -> r.getNombre().name())
                .orElse("TECNICO");

        String token = jwtService.generateToken(userDetails, privilegios);

        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .usuario(usuario.getUsername())
                .rol(rol)
                .privilegios(privilegios)
                .build());
    }
}
