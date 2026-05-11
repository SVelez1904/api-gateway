# Innovatech API Gateway

**Desarrollado por:** Sebastián Vélez  
**Proyecto:** Innovatech Solutions

Este componente actúa como el punto de entrada único al ecosistema de microservicios de **Innovatech**. Su función principal es el enrutamiento de peticiones, la orquestación de la comunicación y la seguridad perimetral del sistema.

---

##  Funcionalidades Principales

*   **Enrutamiento Dinámico:** Redirección centralizada de peticiones hacia los microservicios de Gestión de Proyectos, Usuarios y Analítica.
*   **Seguridad y Filtrado:** Validación de autenticación mediante **JSON Web Tokens (JWT)** generados por la API de Usuarios.
*   **Orquestación:** Manejo centralizado de la comunicación para simplificar el consumo desde el cliente.

##  Stack Tecnológico

*   **Lenguaje:** Java 17+
*   **Framework:** Spring Cloud Gateway
*   **Seguridad:** Spring Security & JWT
*   **Contenedores:** Docker & Docker Compose

##  Despliegue y Ejecución

Para garantizar la correcta conectividad entre este Gateway y los servicios de backend, se recomienda utilizar el archivo **Docker Compose** incluido en la raíz del repositorio principal.


