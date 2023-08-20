package edu.escuelaing.arem.ASE.app;

//import java.io.*;
import java.net.*;

public class URLCatcher {
    public static void main(String[] args) throws Exception {
        try{
            URL miURL = new URL("https://campusvirtual.escuelaing.edu.co:80/moodle/mod/assign/view.php?id=31610");
            System.out.println(miURL.getProtocol());
            System.out.println(miURL.getAuthority());
            System.out.println(miURL.getHost());
            System.out.println(miURL.getPort());
            System.out.println(miURL.getPath());
            System.out.println(miURL.getQuery());
            System.out.println(miURL.getFile());
            System.out.println(miURL.getRef());
        } catch (MalformedURLException e) {
            System.err.println(e);
        }
    }
}
