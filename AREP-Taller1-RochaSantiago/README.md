# Web API Client - Movie Info

Este proyecto es una aplicación de cliente web que se conecta a una API de información de películas para obtener detalles sobre películas específicas. La aplicación permite a los usuarios buscar y obtener información detallada sobre películas utilizando diferentes métodos.

## Arquitectura y Patrones de Diseño

El proyecto sigue una arquitectura de cliente-servidor básica, donde el servidor actúa como un punto de entrada para las solicitudes de los clientes y maneja las solicitudes de manera concurrente utilizando un `ThreadPool` para mejorar la eficiencia y escalabilidad.

La estructura del proyecto sigue el patrón de diseño MVC (Modelo-Vista-Controlador) en su versión más simple, donde:
- `HttpServer` actúa como el controlador que maneja las solicitudes y coordina la lógica de negocio.
- `indexResponse()` y otros métodos relacionados con la generación de respuestas HTML actúan como la vista que define cómo se muestra la información al usuario.
- `cache` y otros métodos relacionados con la gestión de datos actúan como el modelo que almacena y gestiona los datos.

## Buenas Prácticas

Algunas de las buenas prácticas aplicadas en este proyecto incluyen:

- **Concurrencia**: Se utiliza un `ThreadPool` para manejar múltiples solicitudes concurrentes, lo que mejora la eficiencia del servidor.
- **Separación de Responsabilidades**: El patrón MVC se utiliza para separar las responsabilidades y mejorar la mantenibilidad del código.
- **Gestión de Caché**: Se implementa un mecanismo de caché para evitar llamadas innecesarias a la API y mejorar el rendimiento.
- **Manejo de Excepciones**: Se capturan y manejan las excepciones para proporcionar mensajes de error significativos al usuario.
- **Pruebas Unitarias**: Se han creado pruebas unitarias para verificar el funcionamiento correcto de los métodos principales.





