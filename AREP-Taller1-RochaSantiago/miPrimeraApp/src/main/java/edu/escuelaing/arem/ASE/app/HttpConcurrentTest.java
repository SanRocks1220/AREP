package edu.escuelaing.arem.ASE.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConcurrentTest {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(35000);
        final HttpServer httpServer = new HttpServer(serverSocket);

        // Start the server in a separate thread
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                httpServer.startServer();
            }
        });
        serverThread.start();

        // Names of famous animated movies
        String[] movieNames = {
            "The Lion King", "Toy Story", "Finding Nemo", "Shrek",
            "Frozen", "Moana", "Coco", "Despicable Me", "Zootopia",
            "How to Train Your Dragon"
        };

        // Create an array to hold client threads
        Thread[] clientThreads = new Thread[movieNames.length];

        for (int i = 0; i < movieNames.length; i++) {
            final String movieName = movieNames[i];
            clientThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket clientSocket = new Socket("localhost", 35000);
                        String request = "GET /getMovieData?name=" + movieName + " HTTP/1.1\r\n\r\n";
                        clientSocket.getOutputStream().write(request.getBytes());
                        clientSocket.getOutputStream().flush();

                        // Read and print the response from the server
                        byte[] buffer = new byte[4096];
                        int bytesRead = clientSocket.getInputStream().read(buffer);
                        String response = new String(buffer, 0, bytesRead);
                        System.out.println("Response for " + movieName + ":\n" + response);

                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            clientThreads[i].start();
        }

        // Wait for all client threads to finish
        for (Thread clientThread : clientThreads) {
            try {
                clientThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait for a delay and then shut down the server
        try {
            Thread.sleep(10000); // Wait for 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //HttpServer.shutdown();
        //serverThread.interrupt(); // Interrupt the server thread to exit the while loop
        //serverSocket.close();
    }
}
