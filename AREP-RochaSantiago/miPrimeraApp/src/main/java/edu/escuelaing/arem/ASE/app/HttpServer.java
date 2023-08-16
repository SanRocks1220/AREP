package edu.escuelaing.arem.ASE.app;

import java.io.*;
import java.net.*;

public class HttpServer {
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
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            handleRequest(clientSocket);

            clientSocket.close();
        }
        serverSocket.close();
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMovieData(String uriString) throws IOException {
        String movieName = uriString.split("=")[1];
        HttpConnection.setMovieName(movieName);
        HttpConnection.main(null);
        String movieData = HttpConnection.getDataFromApi(); // Modify HttpConnection class to return data as a String
        System.out.println(movieData);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + movieData;
    }

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
                + "    </head>\r\n"
                + "    <body>\r\n"
                + "        <h1>Form with GET</h1>\r\n"
                + "        <form action=\"/getMovieData\">\r\n"
                + "            <label for=\"name\">Movie Name:</label><br>\r\n"
                + "            <input type=\"text\" id=\"name\" name=\"name\" value=\"Avengers\"><br><br>\r\n"
                + "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\r\n"
                + "        </form>\r\n"
                + "        <div id=\"getrespmsg\"></div>\r\n"
                + "\r\n"
                + "        <script>\r\n"
                + "            function loadGetMsg() {\r\n"
                + "                let movieName = document.getElementById(\"name\").value;\r\n"
                + "                const xhttp = new XMLHttpRequest();\r\n"
                + "                xhttp.onload = function() {\r\n"
                + "                    document.getElementById(\"getrespmsg\").innerHTML =\r\n"
                + "                    this.responseText;\r\n"
                + "                }\r\n"
                + "                xhttp.open(\"GET\", \"/getMovieData?name=\"+movieName);\r\n"
                + "                xhttp.send();\r\n"
                + "            }\r\n"
                + "        </script>\r\n"
                + "\r\n"
                + "        <h1>Form with POST</h1>\r\n"
                + "        <form action=\"/getMovieDatapost\">\r\n"
                + "            <label for=\"postname\">Movie Name:</label><br>\r\n"
                + "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"Avengers\"><br><br>\r\n"
                + "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\r\n"
                + "        </form>\r\n"
                + "        \r\n"
                + "        <div id=\"postrespmsg\"></div>\r\n"
                + "        \r\n"
                + "        <script>\r\n"
                + "            function loadPostMsg(name){\r\n"
                + "                let url = \"/getMovieDatapost?name=\" + name.value;\r\n"
                + "\r\n"
                + "                fetch(url, {method: 'POST'})\r\n"
                + "                    .then(x => x.text())\r\n"
                + "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\r\n"
                + "            }\r\n"
                + "        </script>\r\n"
                + "    </body>\r\n"
                + "</html>";
    
        return outputLine;
    }
}
