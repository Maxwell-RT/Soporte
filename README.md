# Microservicio Trasladar

Microservicio REST para la gestión de traslados entre sucursales. Permite crear, aprobar, rechazar, cancelar y finalizar traslados, manteniendo un ciclo de vida controlado por estados.

---

## Herramientas

- Java 25
- Spring Boot 4.0.6
- Spring Data JPA
- MySQL
- Lombok
- Swagger (springdoc-openapi 3.0.3)
- JUnit 5 + Mockito

---

## Modelo de datos

| Campo              | Tipo   | Descripción                          |
|--------------------|--------|--------------------------------------|
| `idTraslado`       | Long   | Identificador único (autogenerado)   |
| `idSucursalOrigen` | Long   | Sucursal que envía                   |
| `idSucursalDestino`| Long   | Sucursal que recibe                  |
| `fechaHora`        | Long   | Timestamp Unix de la operación       |
| `estado`           | Enum   | Estado actual del traslado           |
| `motivo`           | String | Descripción o motivo del traslado    |

---

## Ciclo de vida de estados

```
ESPERA ──► APROBADO ──► FINALIZADO
  │             │
  ▼             ▼
RECHAZADO    CANCELADO
```

Un traslado solo puede cambiar de estado siguiendo este flujo:
- `ESPERA` → `APROBADO` o `RECHAZADO`
- `APROBADO` → `FINALIZADO` o `CANCELADO`

---

## Configuración

### application.properties
```properties
server.port=8088

spring.datasource.url=jdbc:mysql://localhost:3306/trasladar
spring.datasource.username=root
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

### application-test.properties (para tests)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

---

## Endpoints

Base URL: `http://localhost:8088/api/v1/Traslado`

### GET — Listar todos
```
GET /listar
```
Respuesta:
```json
[
  {
    "idTraslado": 1,
    "idSucursalOrigen": 1,
    "idSucursalDestino": 3,
    "fechaHora": 1700000000,
    "estado": "ESPERA",
    "motivo": "Reposición de insumos médicos urgente"
  }
]
```

---

### GET — Buscar por ID
```
GET /listarPorId/{id}
```
Respuesta:
```json
{
  "idTraslado": 1,
  "idSucursalOrigen": 1,
  "idSucursalDestino": 3,
  "fechaHora": 1700000000,
  "estado": "ESPERA",
  "motivo": "Reposición de insumos médicos urgente"
}
```

---

### POST — Crear traslado
```
POST /crear
Content-Type: application/json
```
Body:
```json
{
  "idSucursalOrigen": 1,
  "idSucursalDestino": 3,
  "fechaHora": 1700000000,
  "motivo": "Reposición de insumos médicos urgente"
}
```
El estado se asigna automáticamente a `ESPERA`.

---

### PUT — Aprobar
```
PUT /aprobar/{id}
```

---

### PUT — Rechazar
```
PUT /rechazar/{id}
Content-Type: application/json
```
Body:
```json
{
  "motivo": "Sucursal destino sin capacidad de almacenamiento"
}
```

---

### PUT — Cancelar
```
PUT /cancelar/{id}
```
Solo disponible si el traslado está en estado `APROBADO`.

---

### PUT — Actualizar
```
PUT /actualizar/{id}
Content-Type: application/json
```
Body:
```json
{
  "idSucursalOrigen": 2,
  "idSucursalDestino": 4,
  "fechaHora": 1700100000,
  "motivo": "Traslado reprogramado por cierre de sucursal"
}
```

---

### DELETE — Eliminar
```
DELETE /eliminar/{id}
```
Respuesta: `204 No Content`

---

## Datos de ejemplo (data.sql)

```sql
INSERT INTO traslado (id_sucursal_origen, id_sucursal_destino, fecha_hora, estado, motivo)
VALUES (1, 3, 1700000000, 'ESPERA',     'Reposición de insumos médicos urgente');

INSERT INTO traslado (id_sucursal_origen, id_sucursal_destino, fecha_hora, estado, motivo)
VALUES (2, 5, 1700100000, 'APROBADO',   'Traslado de equipos de radiología');

INSERT INTO traslado (id_sucursal_origen, id_sucursal_destino, fecha_hora, estado, motivo)
VALUES (4, 1, 1700200000, 'RECHAZADO',  'Envío de medicamentos refrigerados');

INSERT INTO traslado (id_sucursal_origen, id_sucursal_destino, fecha_hora, estado, motivo)
VALUES (3, 2, 1700300000, 'CANCELADO',  'Traslado de material quirúrgico');

INSERT INTO traslado (id_sucursal_origen, id_sucursal_destino, fecha_hora, estado, motivo)
VALUES (5, 4, 1700400000, 'FINALIZADO', 'Distribución de vacunas entre sucursales');
```

---

## Tests

El proyecto tiene tres niveles de pruebas:

### TrasladoServiceTest
Prueba la lógica de negocio pura usando Mockito sin levantar Spring.
```bash
./mvnw test -Dtest=TrasladoServiceTest
```

### TrasladoControllerTest
Prueba la capa web con `@WebMvcTest`, mockeando el service.
```bash
./mvnw test -Dtest=TrasladoControllerTest
```

### TrasladoControllerIT
Prueba de integración de la capa web con datos de ejemplo.
```bash
./mvnw test -Dtest=TrasladoControllerIT
```

### Correr todos los tests
```bash
./mvnw test
```

---

## Swagger

Con la aplicación corriendo, la documentación interactiva está disponible en:
```
http://localhost:8088/swagger-ui/index.html
```

---

## Errores comunes

| Código | Causa |
|--------|-------|
| 404 | El traslado con ese ID no existe |
| 405 | Verbo HTTP incorrecto (revisar GET/POST/PUT/DELETE) |
| 409 | El traslado no puede cambiar desde su estado actual |
| 500 | Error interno, revisar logs |