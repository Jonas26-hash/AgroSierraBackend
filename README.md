# AgroSierra API · Unidad 2

Servicio REST Spring Boot 3, Java 17, JPA/H2 y JWT para el registro de labores agrícolas.

## Arranque

Requiere Java 17 y Maven. Desde esta carpeta ejecuta `mvn spring-boot:run`; el servicio escucha en `http://localhost:8080`. H2 usa base en memoria y carga `src/main/resources/data.sql` al iniciar. Consola H2: `/h2-console`.

> En la copia local revisada falta `mvnw.cmd`, por lo que se requiere Maven instalado o recuperar el wrapper del repositorio.

Con Docker Desktop abierto, ejecutar `docker compose up --build`. El backend también se puede correr desde el IDE con Java 17. La imagen compila el proyecto y ejecuta el jar en el puerto 8080.

## Contrato

Rutas versionadas bajo `/api/v1`: `POST /auth/login`; `GET /lotes?pagina&tamanio&valle&estado`; `GET /lotes/{id}`; `GET /lotes/{id}/labores?pagina&tamanio`; `POST /labores`; `PUT /labores/{id}`; `DELETE /labores/{id}`; `POST /lotes/{id}/cerrar-campania`; `GET /usuarios/me`. Login devuelve JWT firmado con vigencia de una hora. Listados usan el envoltorio de paginación común y los errores exponen `status`, `message`, `path` y `validationErrors`.

## Roles semilla

| Usuario | Rol | Privilegios |
| --- | --- | --- |
| `tecnico1` | Técnico | Leer lotes y labores; crear labores |
| `jefe1` | Jefe de campo | Leer, crear, editar y eliminar labores; cerrar campaña |
| `admin` | Administrador | Leer lotes y labores; gestionar usuarios |

Contraseña de demostración: `Agro2026*`.

## Reglas de negocio

- RN-01: no crear ni editar labores en una campaña cerrada.
- RN-02: no editar ni eliminar una labor de más de 24 horas.
- RN-03: no cerrar campaña sin cosecha registrada.
- RN-04: la fumigación respeta la dosis máxima por hectárea.

## Git

Mantener `main`, `develop` y `feature/*`; integrar con `--no-ff`; usar commits `feat:`, `fix:`, `refactor:`, `test:` y `docs:`. Etiquetar la entrega como `v1.0-unidad2` solo en el historial real del repositorio.
