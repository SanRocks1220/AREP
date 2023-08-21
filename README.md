# Web Server Concurrente en Java - Movie Info

Este proyecto consiste en la implementación de un servidor web concurrente en Java que puede manejar múltiples solicitudes de manera simultánea utilizando threads. El servidor es capaz de recibir solicitudes HTTP GET para obtener información sobre películas animadas famosas. Cada solicitud GET incluye el nombre de una película y el servidor responde con detalles sobre esa película, como el título, la fecha de lanzamiento, la duración, el director, el país y el póster.

Con ayuda de **ChatGPT** y **BlackBox**, así como de otro estudiante de la asignatura, se indagó y dió solución al gran número de problemas que se presentaron a lo largo del desarrollo, a nivel de conocimientos sobre tecnologías y métodos, así como a nivel de código e implementaciones. 

## Arquitectura y Patrones de Diseño

El proyecto sigue una arquitectura de cliente-servidor básica, donde el servidor actúa como un punto de entrada para las solicitudes de los clientes y maneja las solicitudes de manera concurrente utilizando un `ThreadPool` para mejorar la eficiencia y escalabilidad.

La estructura del proyecto sigue el patrón de diseño MVC (Modelo-Vista-Controlador) en su versión más simple, donde:
- `HttpServer` actúa como el controlador que maneja las solicitudes y coordina la lógica de negocio.
- `indexResponse()` y otros métodos relacionados con la generación de respuestas HTML actúan como la vista que define cómo se muestra la información al usuario.
- `cache` y otros métodos relacionados con la gestión de datos actúan como el modelo que almacena y gestiona los datos.

## Estrategias de Programación Empleadas

En este proyecto, se aplicaron diversas estrategias de programación:

1. **Programación Multihilo**: Se utilizó la programación multihilo para lograr la concurrencia en el servidor. Cada cliente que se conecta al servidor se maneja en un hilo separado, lo que permite que múltiples solicitudes sean atendidas simultáneamente.
2. **Uso de Threads y ExecutorService**: Los hilos se utilizaron para crear un modelo concurrente en el servidor. Se empleó la clase `ExecutorService` para administrar un pool de hilos que manejan las solicitudes entrantes.
3. **Sockets en Java**: Se utilizó la clase `ServerSocket` para aceptar conexiones entrantes de los clientes y para establecer la comunicación entre el servidor y los clientes.
4. **Manejo de Datos en Caché**: Se implementó un mecanismo de caché para almacenar los datos de las películas consultadas previamente. Esto evita múltiples solicitudes a la API externa para la misma película y mejora la eficiencia.
5. **Interfaz de Usuario HTML**: Se generó una interfaz de usuario simple en HTML para interactuar con el servidor. Se crearon formularios GET y POST para solicitar información sobre películas y se aplicó un estilo CSS básico para mejorar la presentación.
6. **Lógica de Procesamiento de Datos**: Se implementó la lógica para procesar los datos JSON recibidos de la API externa y presentarlos de manera legible en la interfaz de usuario.
7. **Manejo de Excepciones**: Se incorporaron bloques de manejo de excepciones para gestionar posibles errores, como problemas de conexión, lectura y escritura.
8. **Optimización de Recursos**: Se utilizó un `ExecutorService` para gestionar los hilos, lo que optimiza el uso de recursos y permite un manejo eficiente de las solicitudes concurrentes.


También, y de acuerdo a los requerimientos establecidos:
- Se ha implementado un manejo adecuado de la concurrencia utilizando hilos separados para cada cliente. Ahora, el servidor puede manejar múltiples solicitudes de manera concurrente sin bloquear la ejecución.
- La clase `HttpConcurrentTest` simula la conexión de múltiples clientes al servidor. Se crea un array de hilos de cliente, y cada hilo crea una solicitud GET con el nombre de una película y la envía al servidor. Los hilos de cliente se unen después de completar sus solicitudes, asegurando que todos los hilos terminen antes de continuar.
- Se han actualizado los comentarios y las explicaciones en el código para reflejar las nuevas implementaciones y el flujo de ejecución.


## Funcionamiento

El proyecto se compone de las siguientes clases principales:

1. **`HttpServer`**: Esta clase representa el servidor web concurrente. Utiliza un `ServerSocket` para aceptar conexiones de clientes y delega el procesamiento de cada solicitud a threads separados. La clase contiene un `ExecutorService` que administra un pool de threads para manejar las solicitudes de manera concurrente. Aquí se encuentra también el cliente WEB por medio de Html y Js.
2. **`HttpConcurrentTest`**: Se encarga de crear un `HttpServer` y simular la conexión de múltiples clientes al servidor. Para lograr la concurrencia, se utilizan hilos separados para cada cliente, lo que permite enviar solicitudes concurrentes al servidor.
3. **`HttpConnection`**: Esta clase se encarga de realizar la conexión con una API externa para obtener información sobre las películas. En este proyecto, se simula la conexión con una API real utilizando datos en caché para evitar múltiples solicitudes repetitivas.

## Patrones de Programación Seguidos

En la implementación de este servidor web concurrente en Java, se siguieron algunos patrones de programación que contribuyen a la organización y mantenimiento del código. A continuación, se mencionan algunos de los patrones utilizados:

1. **Programación Orientada a Objetos (POO)**: Se aplicó el concepto de POO para estructurar el código en clases y objetos. Cada componente del sistema, como el servidor, las conexiones, el caché y la lógica de procesamiento, se modeló como una clase.
2. **Singleton**: El patrón Singleton se utilizó para asegurarse de que solo exista una instancia de la clase `HttpServer`. Esto garantiza que el servidor sea único y se pueda acceder a él de manera controlada.
3. **Strategy**: El patrón Strategy se aplicó al manejar las diferentes estrategias para obtener los datos de la API o desde la caché. Se definió una interfaz común para ambas estrategias y se implementaron clases concretas para cada una.
4. **Template Method**: Se implementó el patrón Template Method en el método `handleRequest` de la clase `HttpServer`. Este método define el esqueleto de cómo se manejan las solicitudes, mientras que los detalles específicos se implementan en las subclases.

## Extensibilidad del Proyecto

Una característica importante del proyecto es su extensibilidad. El diseño modular y orientado a objetos permite que se puedan realizar ampliaciones y mejoras de manera relativamente sencilla. Algunas formas en las que el proyecto podría ser extendido incluyen:

1. **Soporte para Más Fuentes de Datos**: Actualmente, el proyecto obtiene información de películas de una única fuente externa simulada. Se podría extender el proyecto para conectarse a múltiples fuentes de datos, como APIs de películas en línea, bases de datos o servicios de terceros.
2. **Autenticación y Seguridad**: Se podría agregar autenticación y seguridad al servidor para garantizar que solo los usuarios autorizados puedan acceder a ciertos recursos. Esto podría requerir la implementación de un sistema de autenticación basado en tokens o en otro mecanismo de autenticación.
3. **Ampliación de la Interfaz de Usuario**: La interfaz de usuario actual es bastante simple. Se podría mejorar y ampliar la interfaz para mostrar más detalles sobre las películas, como reseñas de usuarios, calificaciones, trailers, etc.
4. **Gestión de Usuarios y Perfiles**: Si se desea convertir el proyecto en una plataforma más completa, se podría agregar la capacidad de que los usuarios creen perfiles, guarden sus películas favoritas, hagan reseñas y compartan recomendaciones.

## Buenas Prácticas

Al desarrollar el servidor web concurrente, se siguieron una serie de buenas prácticas de programación que mejoran la legibilidad, mantenibilidad y eficiencia del código:

1. **Nombres Descriptivos**: Se utilizaron nombres de variables, métodos y clases descriptivos y significativos. Esto facilita la comprensión del código y reduce la necesidad de comentarios excesivos.
2. **División de Responsabilidades**: Se dividió la funcionalidad en clases y métodos pequeños, cada uno con una responsabilidad específica. Esto facilita la localización de errores y la realización de cambios en el futuro.
3. **Comentarios y Documentación**: Se agregaron comentarios cuando fue necesario explicar la lógica compleja o el propósito de ciertas partes del código. Además, se proporcionó documentación en el código para describir la funcionalidad y el uso de las clases y métodos.
4. **Manejo de Excepciones**: Se implementó un manejo adecuado de excepciones para capturar y manejar errores de manera eficiente. Esto mejora la robustez del sistema y ayuda a identificar y resolver problemas.
5. **Indentación y Formato**: Se aplicó una indentación y un formato coherente en todo el código. Esto mejora la legibilidad y facilita la identificación de bloques de código.
6. **Pruebas**: Se realizaron pruebas exhaustivas para asegurarse de que el servidor funcione correctamente bajo diferentes condiciones. 
7. **Programación "a pares"**:Se utilizó **ChatGPT** así como **BlackBox**, y se estuvo dialogando con otro estudiante de la asignaturapara para identificar y solucionar problemas en el código.
8. **Optimización**: Se implementaron estrategias de optimización, como el uso de caché, para reducir tiempos de búsqueda y mejorar el rendimiento del servidor.

## Implementación por un Proveedor de Servicios

Este proyecto puede servir como base para la implementación de un servicio real de información sobre películas. Aquí hay algunas consideraciones sobre cómo podría ser implementado por un proveedor de servicios:

1. **Escalabilidad**: Un proveedor de servicios debe asegurarse de que su servidor pueda manejar un gran número de solicitudes simultáneas. Esto podría lograrse utilizando técnicas de balanceo de carga, despliegue en la nube y la optimización del rendimiento del servidor.
2. **Monetización**: El proveedor podría explorar modelos de monetización, como la oferta de una suscripción premium con funciones adicionales, publicidad dirigida o incluso la venta de datos de usuarios anonimizados a terceros.
3. **Analíticas**: Para mejorar el servicio y entender mejor las preferencias de los usuarios, se podrían implementar herramientas de análisis para recopilar información sobre las películas más buscadas, las calificaciones más populares, etc.
4. **Integración con Plataformas Externas**: Se podría permitir a los usuarios vincular sus cuentas de redes sociales o servicios de transmisión de películas, lo que permitiría mostrar recomendaciones personalizadas y compartir actividad en línea.


## Cómo Usar Este Proyecto (con Maven)

Este proyecto se ha configurado para utilizar Maven como herramienta de administración de dependencias y construcción. A continuación, se detallan los pasos para ejecutar y probar el servidor web concurrente:

### Requisitos Previos

Asegúrate de tener instalados los siguientes elementos en tu sistema:

1. **Java Development Kit (JDK)**: Se debe tener una versión del JDK instalada en el sistema.
2. **Maven**: Se debe tener Maven instalado.

### Pasos para Ejecutar el Proyecto

1. **Clonar el Repositorio**: Abrir una terminal y navegar hasta el directorio en el que se desee clonar el repositorio.
2. **Compilar el Proyecto**: Haciendo uso de '''mvn compile'''
3. **Ejecutar el Servidor**: ''' mvn exec:java -Dexec.mainClass="edu.escuelaing.arem.ASE.app.HttpServer" '''
4. **Acceder a la Interfaz de Usuario**: Entrando desde un navegador a ''' http://localhost:35000/ '''



## Notas
- Es necesario asegurarse de que los puertos utilizados para el servidor no estén en uso.
- Este proyecto utiliza Java sockets y programación multihilo para lograr la concurrencia. Tener en cuenta que hay otras bibliotecas y frameworks, como Java Servlets y Spring, que pueden proporcionar un enfoque más estructurado y escalable para construir servidores web, pero que por las limitaciones impuestas por el Taller no fueron utilizadas.






