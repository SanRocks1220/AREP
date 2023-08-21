package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase encargada de mantener la conexion y conseguir datos de la API proporcionada.
 * @author Santiago Andrés Rocha C.
 */
public class HttpConnection {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String GET_URL = "https://www.omdbapi.com/";
    private static final String API_KEY = "5e1ae6b4";

    private static String movieName = "";
    private static String reponseString = "";

    /**
     * Metodo pirincipal de la clase.
     * Encargado de mantener la conexion y conseguir datos de la API proporcionada con el metodo especificado.
     * @param args Argumentos para la inicializacion de la clase.
     * @throws IOException Excepcion arrojada en caso de no poder establecer la conexion.
     */
    public static void main(String[] args) throws IOException {

        URL obj = new URL(fullApiURL());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        
        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            reponseString = response.toString();
            System.out.println(reponseString);
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
    }

    /**
     * Construye la URL completa para la API, indicando la clave creada en el registro.
     * @return URL completa, lista para acceder a los recursos que se soliciten.
     */
    public static String fullApiURL(){
        //setMovieName("Wall-e");
        String fullURL = GET_URL + "?t=" + movieName + "&apikey=" + API_KEY;
        System.out.println(fullURL);
        return fullURL;
    }

    /**
     * Getter del nombre de pelicula a buscar.
     * @param name Nombre de pelicula a buscar.
     */
    public static void setMovieName(String name){
        movieName = name;
    }

    /**
     * Setter del nombre de pelicula a buscar.
     * @return Nombre de pelicula a buscar.
     */
    public static String getMovieName(){
        return movieName;
    }

    /**
     * Encargado de entregar a la clase HttpServer la informacion que le retorna la busqueda en la API.
     * @return Informacion solicitada de la pelicula requerida.
     */
    public static String getDataFromApi(){
        return reponseString;
    }

}
