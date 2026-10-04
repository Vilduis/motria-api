# Motria API

API REST para la gestión de talleres mecánicos. Cada taller se registra en la
plataforma y administra sus clientes, vehículos, técnicos y órdenes de servicio
de forma independiente: los datos de un taller nunca son visibles para otro.

## ¿Qué hace?

- **Registro de talleres:** un taller crea su cuenta y su usuario administrador.
- **Clientes y vehículos:** alta, edición y consulta de clientes y de los vehículos de cada uno.
- **Órdenes de servicio:** se asignan a un técnico y avanzan por los estados
  `PENDIENTE → EN_PROCESO → TERMINADO`.
- **Técnicos:** el administrador los da de alta y cada técnico recibe por correo
  una contraseña temporal.
- **Dashboards:** estadísticas generales del taller para el administrador y un
  resumen de órdenes para cada técnico.
- **Cuenta:** inicio de sesión, cambio de contraseña y recuperación por correo.

## Roles

| Rol | Puede |
|---|---|
| **ADMIN** | Gestionar todo el taller: técnicos, usuarios, órdenes y datos del taller |
| **TECHNICAL** | Consultar clientes y vehículos, y actualizar el estado de sus órdenes |

## Tecnologías

- **Java 25** y **Spring Boot 4**
- **Spring Security** con autenticación por **JWT**
- **PostgreSQL** con migraciones gestionadas por **Flyway**
- **Swagger / OpenAPI** para la documentación de los endpoints
- **Docker** para el despliegue

## Ejecutar en local

Requisitos: Java 25 y PostgreSQL.

1. Crea la base de datos `motria_db`.
2. Crea un archivo `application-local.yaml` en la raíz del proyecto (no se sube al repositorio):

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/motria_db
       username: postgres
       password: 'tu_contraseña'

   app:
     jwt:
       secret: <clave en Base64 de al menos 32 bytes>
   ```

3. Arranca la API:

   ```bash
   ./mvnw spring-boot:run
   ```

Las tablas se crean automáticamente al arrancar.

| | URL |
|---|---|
| API | http://localhost:8080/api |
| Documentación (Swagger) | http://localhost:8080/api/docs |
| Estado | http://localhost:8080/api/actuator/health |
