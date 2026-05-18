# api-gateway
[![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud-Gateway-brightgreen)](https://spring.io/projects/spring-cloud-gateway)
[![JWT](https://img.shields.io/badge/JWT-Authentication-black?logo=jsonwebtokens)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Container-blue?logo=docker)](https://www.docker.com/)

Este microservicio (`api-gateway`) actúa como el **punto de entrada único** al ecosistema de InnovaTech. Su responsabilidad principal es interceptar todas las peticiones HTTP externas, validar la autenticación mediante tokens JWT emitidos por la API de Usuarios (`api-usuarios`) y enrutar las solicitudes de forma segura hacia los servicios correspondientes (`api-proyectos` y `api-analytics`) dentro de la red de Docker.

---

## Flujo de Autenticación y Validación

1. **Generación:** El cliente solicita un token enviando sus credenciales a `api-usuarios` a través del Gateway (Ruta expuesta públicamente).
2. **Petición Protegida:** Para consumir recursos de proyectos o analíticas, el cliente debe incluir el token en la cabecera: `Authorization: Bearer <JWT_TOKEN>`.
3. **Validación en Gateway:** El Gateway intercepta la petición, extrae el token, valida su firma, expiración y claims (usando la misma clave secreta que la API de usuarios) **antes** de permitir que la petición toque los servicios internos.
4. **Despacho:** Si el token es válido, el Gateway redirige la petición al contenedor correspondiente de forma transparente.

---

## Mapa de Enrutamiento (Routing Matrix)

Al estar todo dockerizado, el Gateway redirige el tráfico usando los nombres de los contenedores de Docker como host internos:

| Ruta Externa (Cliente) | Destino Interno (Docker) | Tipo de Acceso | Servicio Destino |
| :--- | :--- | :--- | :--- |
| `/api/usuarios/login**` | `http://api-usuarios:8080/api/auth/**` | **Público** | API Usuarios (Login/Registro) |
| `/api/proyectos/**` | `http://api-proyectos:8081/api/proyectos/**` |  **Protegido (JWT)** | API Proyectos (Core) |
| `/api/analytics/**` | `http://api-analytics:8082/api/analytics/**` |  **Protegido (JWT)** | API Analytics |

---

## Configuración del Entorno Docker (`application.yml`)

El enrutamiento y el filtro de seguridad personalizado se configuran de la siguiente manera dentro del contenedor:

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        # Ruta Pública: Autenticación y creación de tokens
        - id: api-usuarios-public
          uri: http://api-usuarios:8080
          predicates:
            - Path=/api/auth/**

        # Ruta Protegida: Gestión de proyectos
        - id: api-proyectos-protected
          uri: http://api-proyectos:8081
          predicates:
            - Path=/api/proyectos/**
          filters:
            - JwtAuthenticationFilter # Filtro personalizado para validar el token

        # Ruta Protegida: Analíticas
        - id: api-analytics-protected
          uri: http://api-analytics:8082
          predicates:
            - Path=/api/analytics/**
          filters:
            - JwtAuthenticationFilter

# Clave secreta compartida con api-usuarios para validar la firma del token
jwt:
  secret: "${JWT_SECRET_KEY:TuClaveSecretaSuperSeguraParaFirmarLosTokensJWT2026}"
