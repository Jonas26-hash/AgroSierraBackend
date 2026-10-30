# AgroSierra API

Backend REST del caso AgroSierra (Unidad 2): autenticación JWT, autorización por privilegios, consulta de lotes, CRUD de labores, cierre de campaña y reglas de negocio RN-01 a RN-04.

**Stack:** Java 17, Spring Boot 3.3.5, Spring Security, Spring Data JPA, H2, BCrypt, JJWT 0.12.6, springdoc-openapi 2.6.0 y Maven.

## Estado

- Servicio con rutas versionadas bajo `/api/v1`.
- Login con contraseña cifrada BCrypt y token JWT HS256 de una hora.
- Autorización por privilegio mediante `@PreAuthorize`, con privilegios cargados desde la base de datos.
- CRUD de labores, cierre de campaña y reglas RN-01 a RN-04.
- Paginación uniforme y manejo centralizado de errores.
- Documentación OpenAPI/Swagger.
- 8 pruebas de integración.

## Estructura de paquetes

```
pe.upeu.agrosierra
├── Aplicacion.java
├── config/
│   ├── ApplicationConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── ManejadorDeErrores.java
├── dominio/          Labor, Lote, TipoLabor
├── repositorio/      LaborRepository, LoteRepository, RolRepository, UsuarioRepository
├── seguridad/        AuthController, Privilegio, Rol, Usuario
├── servicio/         LaborService, LoteService
└── web/              LaborController, LoteController, UsuarioController
    └── dto/          DTO de petición, respuesta, paginación y error
```

Recursos:

- `src/main/resources/application.yml`: configuración del servicio.
- `src/main/resources/data.sql`: roles, privilegios, usuarios y datos semilla.
- `src/test/java/pe/upeu/agrosierra/IntegrationTest.java`: pruebas de integración.

## Requisitos y ejecución

Requiere **Java 17** y **Maven 3.9 o superior**. El repositorio no incluye Maven Wrapper.

### 1. Definir el secreto JWT

El secreto no se versiona. Debe tener al menos 32 caracteres (256 bits) para HS256.

PowerShell:

```powershell
$env:JWT_SECRET = "una-clave-de-al-menos-32-caracteres"
mvn spring-boot:run
```

Bash:

```bash
export JWT_SECRET="una-clave-de-al-menos-32-caracteres"
mvn spring-boot:run
```

Si `JWT_SECRET` no está definido, `JwtService` genera una clave efímera al arrancar y registra una advertencia. Ese modo sirve solo para desarrollo local: todos los tokens dejan de ser válidos al reiniciar y no debe usarse en producción.

### 2. Arrancar el servicio

```bash
mvn spring-boot:run
```

El servicio escucha en `http://localhost:8080`.

- Consola H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:agrosierra`
  - Usuario: `sa`
  - Contraseña: vacía
- Swagger UI: `http://localhost:8080/swagger-ui.html` (redirige a `/swagger-ui/index.html`)
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

H2 es una base **en memoria** con `ddl-auto: create-drop`; `data.sql` se carga en cada arranque y los datos no persisten al detener el servicio.

### 3. Docker

Con Docker Desktop abierto:

```powershell
$env:JWT_SECRET = "una-clave-de-al-menos-32-caracteres"
docker compose up --build
```

`compose.yaml` pasa `JWT_SECRET` al contenedor. La imagen compila el proyecto y ejecuta el jar en el puerto 8080.

### 4. Ejecutar las pruebas

```bash
mvn -B test
```

## Contrato del API

Todas las rutas, salvo el login, requieren:

```
Authorization: Bearer <token>
```

| Método | Ruta | Privilegio | Respuestas esperadas |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/login` | Público | `200`, `400`, `401` |
| `GET` | `/api/v1/lotes?pagina&tamanio&valle&estado` | `LOTE_LEER` | `200`, `401`, `403` |
| `GET` | `/api/v1/lotes/{id}` | `LOTE_LEER` | `200`, `401`, `403`, `404` |
| `GET` | `/api/v1/lotes/{id}/labores?pagina&tamanio` | `LABOR_LEER` | `200`, `401`, `403`, `404` |
| `POST` | `/api/v1/labores` | `LABOR_CREAR` | `201`, `400`, `401`, `403`, `404`, `409` |
| `PUT` | `/api/v1/labores/{id}` | `LABOR_EDITAR` | `200`, `400`, `401`, `403`, `404`, `409` |
| `DELETE` | `/api/v1/labores/{id}` | `LABOR_ELIMINAR` | `204`, `401`, `403`, `404` |
| `POST` | `/api/v1/lotes/{id}/cerrar-campania` | `CAMPANIA_CERRAR` | `200`, `401`, `403`, `404`, `409` |
| `GET` | `/api/v1/usuarios/me` | Autenticado | `200`, `401` |

Parámetros de paginación: `pagina` (por defecto `0`) y `tamanio` (por defecto `20`). El listado de lotes acepta además `valle` y `estado` (`abierta`/`cerrada` o `true`/`false`).

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "tecnico1",
  "password": "<contraseña del anexo privado>"
}
```

Respuesta `200`:

```json
{
  "token": "<jwt>",
  "usuario": "tecnico1",
  "rol": "TECNICO",
  "privilegios": ["LOTE_LEER", "LABOR_LEER", "LABOR_CREAR", "LABOR_EDITAR"]
}
```

Credenciales incorrectas producen `401` con mensaje uniforme.

### Labor

```http
POST /api/v1/labores
Authorization: Bearer <token>
Content-Type: application/json

{
  "loteId": 1,
  "tipo": "RIEGO",
  "fecha": "2026-10-27",
  "responsable": "tecnico1",
  "cantidad": 10.0,
  "observacion": "Riego matutino"
}
```

`tipo` admite `RIEGO`, `FUMIGACION` y `COSECHA`; `fecha` usa formato ISO-8601 (`AAAA-MM-DD`). El responsable efectivo se toma del usuario autenticado.

Respuesta `201`:

```json
{
  "id": 21,
  "loteId": 1,
  "tipo": "RIEGO",
  "fecha": "2026-10-27",
  "responsable": "tecnico1",
  "cantidad": 10.0,
  "observacion": "Riego matutino"
}
```

### Paginación

```json
{
  "contenido": [],
  "pagina": 0,
  "tamanio": 20,
  "totalElementos": 0,
  "totalPaginas": 0,
  "ultima": true
}
```

### Errores

Formato uniforme:

```json
{
  "timestamp": "2026-10-27T18:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos invalidos",
  "path": "/api/v1/labores",
  "validationErrors": {
    "cantidad": "La cantidad aplicada supera la dosis maxima permitida"
  }
}
```

- `400`: validación de campos o RN-04.
- `401`: token ausente, alterado o vencido; credenciales incorrectas en el login.
- `403`: autenticado sin el privilegio requerido, o RN-02.
- `404`: recurso inexistente.
- `409`: RN-01 o RN-03.
- `500`: error no controlado.

## Roles y privilegios

Los privilegios se asignan en `data.sql` y se verifican por autoridad, no por nombre de usuario.

| Rol | Privilegios |
| --- | --- |
| `TECNICO` | `LOTE_LEER`, `LABOR_LEER`, `LABOR_CREAR`, `LABOR_EDITAR` |
| `JEFE_CAMPO` | `LOTE_LEER`, `LABOR_LEER`, `LABOR_CREAR`, `LABOR_EDITAR`, `LABOR_ELIMINAR`, `CAMPANIA_CERRAR` |
| `ADMINISTRADOR` | `LOTE_LEER`, `LABOR_LEER`, `USUARIO_GESTIONAR` |

Usuarios semilla: `tecnico1`, `jefe1` y `admin`. La contraseña de demostración no se publica en este repositorio; se entrega en el anexo privado de la evaluación.

## Reglas de negocio

| Regla | Comportamiento | Respuesta |
| --- | --- | --- |
| RN-01 | No se puede registrar ni corregir una labor sobre un lote con campaña cerrada | `409` |
| RN-02 | Un técnico solo corrige sus propias labores dentro de las 24 horas siguientes. Pasado el plazo, o si la labor es de otro técnico, se rechaza. El jefe de campo puede corregir labores de otros usuarios; si él mismo registró la labor, también se le aplica el plazo de 24 horas | `403` |
| RN-03 | No se puede cerrar la campaña de un lote sin al menos una labor de tipo `COSECHA` | `409` |
| RN-04 | En una fumigación, la cantidad no puede superar la dosis máxima por hectárea multiplicada por las hectáreas del lote | `400` con detalle en `validationErrors.cantidad` |

## Seguridad

- Sesiones sin estado (`SessionCreationPolicy.STATELESS`) y CSRF deshabilitado por tratarse de una API con token.
- Contraseñas almacenadas con BCrypt.
- Token JWT firmado con HS256; incluye el usuario como `subject` y los privilegios como claim. Vigencia: una hora. No hay renovación.
- `JwtAuthenticationFilter` valida el token en cada petición.
- Un token ausente, alterado o vencido produce `401` con cuerpo JSON uniforme; la falta de privilegio produce `403`.
- El secreto se configura con la variable de entorno `JWT_SECRET`; no se versiona ningún valor real.
- La consola H2, Swagger y `/v3/api-docs` están abiertos sin autenticación para facilitar el desarrollo local. En producción deben restringirse o deshabilitarse.

## Pruebas

```bash
mvn -B test
```

Resultado en la validación local: **8 pruebas, 0 fallos**.

- `loginCorrecto`
- `loginIncorrecto`
- `rutaSinToken401`
- `listadoLotesPaginado`
- `tecnicoNoPuedeCerrarCampania`
- `jefePuedeCerrarCampaniaConCosecha`
- `rn01LaborSobreLoteCerrado`
- `rn04FumigacionExcedeDosis`

Las pruebas usan `MockMvc` sobre el contexto completo de Spring Boot y la base H2 en memoria.

## Aplicación móvil relacionada

Este backend proporciona la API REST utilizada por la aplicación AgroSierra.

- Aplicación móvil: https://github.com/Jonas26-hash/AgroSierra

## Limitaciones conocidas

- H2 en memoria con `create-drop`: la información se pierde al reiniciar y no hay migraciones ni perfil de base de datos persistente.
- No se incluye Maven Wrapper; se requiere Maven instalado.
- No hay endpoints de administración de usuarios: `USUARIO_GESTIONAR` está definido, pero solo existe `GET /api/v1/usuarios/me`.
- No hay renovación de token ni cierre de sesión del lado del servidor.
- El cierre de campaña no valida el estado previo del lote; si ya está cerrado, la operación vuelve a responder `200`.
- Los datos semilla usan fechas fijas de octubre de 2026; RN-02 se evalúa contra la fecha real del sistema.
