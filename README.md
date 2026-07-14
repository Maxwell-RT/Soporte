# 🎫 Microservicio de Soporte — Sky

Microservicio REST para la gestión de tickets de soporte técnico. Permite crear, consultar, actualizar, cerrar y eliminar tickets, con validación de usuario contra el microservicio de usuarios.

---

## 🛠️ Herramientas y Tecnologias

| Tecnología | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | — |
| Hibernate | 7.2.12 |
| MySQL | 8.0 |
| springdoc-openapi | 3.0.3 |
| JUnit 5 + Mockito | — |
| Maven | — |

---

## ⚙️ Configuración

### Requisitos previos

- Java 25+
- MySQL 8.0 corriendo en `localhost:3306`
- Microservicio de Usuarios corriendo en `localhost:8083`

### `application.properties`

```properties
server.port=8084

spring.datasource.url=jdbc:mysql://localhost:3306/soporte_db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

springdoc.springdoc-hateoas-enabled=false
```

### Crear la base de datos

```sql
CREATE DATABASE IF NOT EXISTS soporte_db;
```

Hibernate crea la tabla `soporte` automáticamente al arrancar con `ddl-auto=update`.

---

## 🚀 Cómo ejecutar

```bash
# Clonar el repositorio
git clone <url-del-repo>
cd Soporte

# Compilar y ejecutar
mvn spring-boot:run
```

La aplicación queda disponible en `http://localhost:8084`.

---

## 📦 Modelo — Ticket de Soporte

```json
{
  "idSoporte": 1,
  "idUsuario": 42,
  "asunto": "No puedo acceder a mi cuenta",
  "descripcion": "Al intentar iniciar sesión aparece el error 403.",
  "estado": true
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `idSoporte` | `Long` | ID autoincremental (generado por BD) |
| `idUsuario` | `Long` | ID del usuario que abre el ticket |
| `asunto` | `String` | Título breve del problema |
| `descripcion` | `String` | Detalle del problema |
| `estado` | `boolean` | `true` = abierto · `false` = cerrado |

> **Nota:** `idSoporte` y `estado` no se envían al crear — los gestiona el sistema automáticamente.

---

## 🔌 Endpoints

Base URL: `http://localhost:8084/api/v1/soporte`

### POST `/` — Crear ticket

Valida que el usuario exista en el microservicio de usuarios antes de crear el ticket.

**Request body:**
```json
{
  "idUsuario": 42,
  "asunto": "No puedo acceder a mi cuenta",
  "descripcion": "Al intentar iniciar sesión aparece el error 403."
}
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `201 Created` | Ticket creado correctamente |
| `404 Not Found` | El usuario no existe en el sistema |
| `400 Bad Request` | Error genérico al guardar |

---

### GET `/` — Obtener todos los tickets

```
GET http://localhost:8084/api/v1/soporte
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `200 OK` | Lista de tickets (puede ser vacía) |

---

### GET `/{ticketId}` — Obtener ticket por ID

```
GET http://localhost:8084/api/v1/soporte/1
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `200 OK` | Ticket encontrado |
| `404 Not Found` | Ticket no existe |

---

### GET `/usuario/{usuarioId}` — Obtener tickets por usuario

```
GET http://localhost:8084/api/v1/soporte/usuario/42
```

Devuelve todos los tickets asociados a un `idUsuario`. Retorna lista vacía si el usuario no tiene tickets, sin lanzar error.

**Respuestas:**

| Código | Descripción |
|---|---|
| `200 OK` | Lista de tickets del usuario (puede ser vacía) |

---

### PUT `/{ticketId}` — Actualizar ticket

```
PUT http://localhost:8084/api/v1/soporte/1
```

Actualiza `asunto`, `descripcion` y `estado` del ticket.

**Request body:**
```json
{
  "asunto": "Asunto actualizado",
  "descripcion": "Descripción actualizada",
  "estado": true
}
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `200 OK` | Ticket actualizado |
| `404 Not Found` | Ticket no existe |

---

### PUT `/{ticketId}/cerrar` — Cerrar ticket

```
PUT http://localhost:8084/api/v1/soporte/1/cerrar
```

Cambia el `estado` del ticket a `false` (cerrado). No requiere body.

**Respuestas:**

| Código | Descripción |
|---|---|
| `200 OK` | Ticket cerrado (`estado: false`) |
| `404 Not Found` | Ticket no existe |

---

### DELETE `/{ticketId}` — Eliminar ticket

```
DELETE http://localhost:8084/api/v1/soporte/1
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `204 No Content` | Ticket eliminado |
| `404 Not Found` | Ticket no existe |

---

## 🔗 Integración con microservicio de Usuarios

Al crear un ticket, el servicio consulta:

```
GET http://localhost:8083/api/v1/usuarios/{idUsuario}
```

- Si el usuario existe → se crea el ticket con `estado = true`.
- Si retorna `404` → se lanza `RuntimeException` y el controller responde `404 Not Found`.
- Si el servicio no está disponible → se lanza `RuntimeException` y el controller responde `400 Bad Request`.

---

## 🧪 Tests

### Ejecutar todos los tests

```bash
mvn test
```

### Perfil de test

Los tests usan el perfil `test` con base de datos H2 en memoria. Crea `src/test/resources/application-test.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Cobertura de tests

| Clase | Tipo | Casos cubiertos |
|---|---|---|
| `SoporteApplicationTests` | Spring Context | Carga del contexto, existencia de la clase principal |
| `SoporteControllerIT` | Integration Test | Todos los endpoints: éxito y error por cada ruta |

---

## 📁 Estructura del proyecto

```
src/
├── main/
│   ├── java/com/sky/Soporte/
│   │   ├── SoporteApplication.java       # Clase principal + RestTemplate bean
│   │   ├── controller/
│   │   │   └── SoporteController.java    # Endpoints REST
│   │   ├── service/
│   │   │   └── SoporteService.java       # Lógica de negocio
│   │   ├── repository/
│   │   │   └── SoporteRepository.java    # JPA Repository
│   │   └── model/
│   │       ├── Soporte.java              # Entidad principal
│   │       └── UsuarioDTO.java           # DTO para consumir microservicio
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/sky/Soporte/
    │   ├── SoporteApplicationTests.java
    │   └── Controller/
    │       └── SoporteControllerIT.java
    └── resources/
        └── application-test.properties
```

---

## 👥 Dependencias entre microservicios

```
[Postman / Cliente]
        │
        ▼
[Soporte :8084]  ──────►  [Usuarios :8083]
        │
        ▼
  [MySQL :3306]
   soporte_db
```