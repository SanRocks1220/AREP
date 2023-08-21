package edu.escuelaing.arem.ASE.app;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Crea una conexion para App Web en la que solicitar informacion sobre peliculas directo de la API.
 * @author Santiago Andrés Rocha C.
 */
public class HttpServer {

    static ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    static ExecutorService threadPool = Executors.newFixedThreadPool(10); // Puedes ajustar el número de hilos

    /** 
     * Metodo principal de la clase HttpServer.
     * Encargada de crear el socket de conexion y arrancar el servidor para recibir solicitudes.
     * @param args Argumentos para la inicializacion de la clase.
     * @throws IOException Excepcion arrojada en caso de no poder establecer la conexion.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();

                //
                // Aquí empieza la posibilidad de concurrencia
                //
                final Socket finalClientSocket = clientSocket;
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        handleRequest(finalClientSocket);
                    }
                });
                //
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            // Ya no son necesarios debido a la concurrencia
            //handleRequest(clientSocket);
            //clientSocket.close();
        }
        serverSocket.close();
    }

    
    /** 
     * Encargado de procesar las solicitudes realizadas desde el cliente.
     * @param clientSocket Socket destinado a la conexion.
     */
    public static void handleRequest(Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            boolean firtsLine = true;
            String uriString = "";

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (firtsLine) {
                    firtsLine = false;
                    uriString = inputLine.split(" ")[1];
                    System.out.println("Uri: " + uriString);
                }
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println("Uri: " + uriString);

            String outputLine = "";
            if (uriString.startsWith("/getMovieData")) {
                outputLine = getMovieData(uriString);
            } else {
                outputLine = indexResponse();
            }

            out.println(outputLine);
            out.close();
            in.close();

            // Cerrar el socket después de completar la solicitud
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * Conecta con la clase HttpConnection y solicita informacion sobre las peliculas indicadas.
     * @param uriString uri para tomar el nombre de la pelicula.
     * @return String Formato para poder imprimir el resultado JSON retornado.
     * @throws IOException Excepcion arrojada en caso de no poder establecer la conexion.
     */
    public static String getMovieData(String uriString) throws IOException {
        String movieName = uriString.split("=")[1];
        if(inCache(movieName)){
            return "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + htmlFormat(processJson(getFromCache(movieName)));
        } else {
            HttpConnection.setMovieName(movieName);
            HttpConnection.main(null);
            String movieData = HttpConnection.getDataFromApi(); // Modify HttpConnection class to return data as a String

            saveInCache(movieName, movieData);

            return "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + htmlFormat(processJson(movieData));
        }
        
    }

    
    /** 
     * Verifica si la pelicula ya ha sido consultada o no anteriormente.
     * @param movieName Nombre de la pelicula a buscar en cache.
     * @return boolean True si ya se ha buscado previamente, False si es la primera vez.
     */
    public static boolean inCache(String movieName){
        boolean isIn = false;
        if(cache.containsKey(movieName)){
            isIn = true;
        }
        return isIn;
    }

    
    /** 
     * Retorna los datos almacenados en cache de la pelicula que se solicite.
     * @param movieName Nombre de la pelicula de la que tomar los datos en cache.
     * @return String Datos completos en JSON sobre la pelicula solicitada
     */
    public static String getFromCache(String movieName){
        System.out.println("Ya está en caché, no busca en API");
        return cache.get(movieName);
    }

    
    /** 
     * Almacena en cache los datos de la pelicula una vez esta sea buscada.
     * @param movieName Nombre de la pelicula a la que asociar los datos.
     * @param movieData Datos de la pelicula a guardar asociados a su nombre.
     */
    public static void saveInCache(String movieName, String movieData){
        System.out.println("No está en caché, busca en API");
        cache.put(movieName, movieData);
    }

    /**
     * Procesa datos en formato JSON como Strings para permitir imprimir correctamente en la pantalla.
     * @param jsonString Datos en formato JSON a ser procesados y organizados correctamente.
     * @return Datos ajustados para poder ser mostrados en pantalla.
     */
    public static String processJson(String jsonString){
        String charsToRemove = "\"{}";

        for (char c : charsToRemove.toCharArray()) {
            jsonString = jsonString.replace(String.valueOf(c), "");
        }

        String[] dataParts = jsonString.split(",");

        String title = getCaracts(dataParts, "Title:");
        String released = getCaracts(dataParts, "Released:");
        String Runtime = getCaracts(dataParts, "Runtime:");
        String director = getCaracts(dataParts, "Director:");
        String country = getCaracts(dataParts, "Country:");
        String poster = getCaracts(dataParts, "Poster:");

        String rawData = "";
        rawData += "Title: " + title + "\n";
        rawData += "Released: " + released + "\n";
        rawData += "Runtime: " + Runtime + "\n";
        rawData += "Director: " + director + "\n";
        rawData += "Country: " + country + "\n";
        rawData += "Poster: " + poster + "\n";

        return rawData;
    }

    /**
     * Separa el nombre de una caracteristica con su valor asociado, de tal forma que se obtenga la informacion
     * asociada en un dato independiente.
     * @param dataParts Conjunto de caracteristicas con sus valores asociados.
     * @param key Nombre de la caracteristica a separar de su valore asociado.
     * @return Valor de la caracteristica indicada separada de su nombre.
     */
    public static String getCaracts(String[] dataParts, String key) {
        for (String part : dataParts) {
            if (part.startsWith(key)) {
                return part.substring(key.length());
            }
        }
        return "";
    }

    
    /** 
     * Da formato a un String de forma que pueda ser impreso correctamente en pantalla como Html.
     * @param rawData Datos a dar formato para impresion en pantalla.
     * @return String Datos con formato Html para poder imprimir en pantalla.
     */
    public static String htmlFormat(String rawData){
        StringBuilder fromStrToHtml = new StringBuilder();
        String moviePoster = "";
        fromStrToHtml.append("<ul>\n");
        
        String[] lines = rawData.split("\n");
        for (String line : lines) {
            String[] parts = line.split(": ");

            if (parts.length == 2 && !parts[0].equals("Poster")) {
                fromStrToHtml.append("<li><b>").append(parts[0]).append(":</b> ").append(parts[1]).append("</li>\n");
            } else {
                fromStrToHtml.append("<li><b>").append(parts[0]).append(":</b> ").append("</li>\n");
                moviePoster = parts[1];
            }
        }

        fromStrToHtml.append("</ul>\n");
        fromStrToHtml.append("<center><img src=").append(moviePoster).append(" /></center>");
        return fromStrToHtml.toString();
    }
    
    /**
     * Genera los formularos para retornar informacion solicitada.
     * Hace uso del metodo getCSS() para agregar estilo al momento de enseñarse en pantalla.
     * @return String Formularios con GET y POST en su formato HTML, con un CSS basico aplicado
     */
    public static String indexResponse() {
        String outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\r\n"
                + "<html>\r\n"
                + "    <head>\r\n"
                + "        <title>Form Example</title>\r\n"
                + "        <meta charset=\"UTF-8\">\r\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
                + getCSS()
                + "    </head>\r\n"
                + "    <body>\r\n"
                + "        <div class=\"feedback-card\">\r\n"
                + "            <div class=\"feedback-header\">\r\n"
                + "                <h1>CONSULT MOVIES BY NAME (GET)</h1>\r\n"
                + "            </div>\r\n"
                + "            <div class=\"feedback-body\">\r\n"
                + "                <form action=\"/getMovieData\">\r\n"
                + "                    <label for=\"name\">Movie Name:</label><br>\r\n"
                + "                    <input type=\"text\" id=\"name\" name=\"name\" value=\"\"><br><br>\r\n"
                + "                    <button type=\"button\" onclick=\"loadGetMsg()\">Submit</button>\r\n"
                + "                </form>\r\n"
                + "                <div class=\"feedback-body__message\" id=\"getrespmsg\"></div>\r\n"
                + "            </div>\r\n"
                + "        </div>\r\n"
                + "\r\n"
                + "        <div class=\"feedback-card\">\r\n"
                + "            <div class=\"feedback-header\">\r\n"
                + "                <h1>CONSULT MOVIES BY NAME (POST)</h1>\r\n"
                + "            </div>\r\n"
                + "            <div class=\"feedback-body\">\r\n"
                + "                <form action=\"/getMovieDatapost\">\r\n"
                + "                    <label for=\"postname\">Movie Name:</label><br>\r\n"
                + "                    <input type=\"text\" id=\"postname\" name=\"name\" value=\"\"><br><br>\r\n"
                + "                    <button type=\"button\" onclick=\"loadPostMsg(postname)\">Submit</button>\r\n"
                + "                </form>\r\n"
                + "                <div class=\"feedback-body__message\" id=\"postrespmsg\"></div>\r\n"
                + "            </div>\r\n"
                + "        </div>\r\n"
                + "\r\n"
                + "        <script>\r\n"
                + "            function loadGetMsg() {\r\n"
                + "                let movieName = document.getElementById(\"name\").value;\r\n"
                + "                const xhttp = new XMLHttpRequest();\r\n"
                + "                xhttp.onload = function() {\r\n"
                + "                    document.getElementById(\"getrespmsg\").innerHTML =\r\n"
                + "                    this.responseText;\r\n"
                + "                }\r\n"
                + "                xhttp.open(\"GET\", \"/getMovieData?name=\" + movieName);\r\n"
                + "                xhttp.send();\r\n"
                + "            }\r\n"
                + "\r\n"
                + "            function loadPostMsg(name) {\r\n"
                + "                let url = \"/getMovieDatapost?name=\" + name.value;\r\n"
                + "                fetch(url, { method: 'POST' })\r\n"
                + "                    .then(x => x.text())\r\n"
                + "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\r\n"
                + "            }\r\n"
                + "        </script>\r\n"
                + "    </body>\r\n"
                + "</html>";
    
        return outputLine;
    }
    
    /**
     * Define y aplica el estilo de la pagina a mostrar en pantalla.
     * @return Estilo a añadir al Html para enseñarse en pantalla.
     */
    public static String getCSS() {
        return "<style>\r\n"
                + "    /* Tu estilo CSS personalizado aquí */\r\n"
                + "    font-family: 'Roboto', sans-serif;\r\n"
                + "    body {\r\n"
                + "        margin: 0;\r\n"
                + "        padding: 0;\r\n"
                + "        background-color: #a2a2a2;\r\n"
                + "        display: flex;\r\n"
                + "        justify-content: center;\r\n"
                + "        align-items: center;\r\n"
                + "        flex-direction: column;\r\n"
                + "        height: 100vh;\r\n"
                + "    }\r\n"
                + "    input, button, textarea, img {\r\n"
                + "        border: 2px solid rgba(0, 0, 0, 0.6);\r\n"
                + "        background-image: none;\r\n"
                + "        background-color: #dadad3;\r\n"
                + "        box-shadow: none;\r\n"
                + "        padding: 5px;\r\n"
                + "    }\r\n"
                + "    input:focus, button:focus, textarea:focus {\r\n"
                + "        outline: none;\r\n"
                + "    }\r\n"
                + "    textarea {\r\n"
                + "        min-height: 50px;\r\n"
                + "        resize: vertical;\r\n"
                + "    }\r\n"
                + "    button {\r\n"
                + "        cursor: pointer;\r\n"
                + "        font-weight: 500;\r\n"
                + "    }\r\n"
                + "    .feedback-card {\r\n"
                + "        border: 1px solid black;\r\n"
                + "        max-width: 980px;\r\n"
                + "        background-color: #fff;\r\n"
                + "        margin: 0 auto;\r\n"
                + "        box-shadow: -0.6rem 0.6rem 0 rgba(29, 30, 28, 0.26);\r\n"
                + "    }\r\n"
                + "    .feedback-header {\r\n"
                + "        text-align: center;\r\n"
                + "        padding: 8px;\r\n"
                + "        font-size: 14px;\r\n"
                + "        font-weight: 700;\r\n"
                + "        border-bottom: 1px solid black;\r\n"
                + "    }\r\n"
                + "    .feedback-body {\r\n"
                + "        padding: 20px;\r\n"
                + "        display: flex;\r\n"
                + "        flex-direction: column;\r\n"
                + "    }\r\n"
                + "    .feedback-body__message {\r\n"
                + "        margin-top: 10px;\r\n"
                + "    }\r\n"
                + "    .feedback-body button {\r\n"
                + "        margin-top: 10px;\r\n"
                + "        align-self: flex-end;\r\n"
                + "    }\r\n"
                + "</style>\r\n";

                //
                // Nota por Santiago Rocha.
                // Plantilla tomada de:
                // https://plantillashtmlgratis.com/efectos-css/formularios-de-contacto-css/formulario-de-retroalimentacion-de-interfaz-de-usuario-retro/
                //
    }

    // Se agrega un método shutdown para detener el ThreadPool cuando sea necesario
    /**
     * Encargado de terminar y cerrar las conecciones cuando se trabaja con concurrencia.
     */
    public static void shutdown() {
        threadPool.shutdown();
    }
}
