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
        //System.out.println(movieData);
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
                + getCSS()
                + "    </head>\r\n"
                + "    <body>\r\n"
                + "        <div class=\"feedback-card\">\r\n"
                + "            <div class=\"feedback-header\">\r\n"
                + "                <h1>Consult Movies by Name (GET)</h1>\r\n"
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
                + "                <h1>Consult Movies by Name (POST)</h1>\r\n"
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
    

    public static String getCSS() {
        return "<style>\r\n"
                + "    /* Tu estilo CSS personalizado aquí */\r\n"
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
                + "    input, button, textarea {\r\n"
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
    }
    
}
