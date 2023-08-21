package edu.escuelaing.arem.ASE.app;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Encargado de testear las funcionalidades y métodos de la clase HttpConnection.
 * @author Santiago Andrés Rocha C.
 */
public class HttpConnectionTest {

    @Test
    public void testFullApiURL() {
        HttpConnection.setMovieName("Movie1");
        String expectedURL = "https://www.omdbapi.com/?t=Movie1&apikey=5e1ae6b4";
        String actualURL = HttpConnection.fullApiURL();
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void testGetMovieName() {
        String movieName = "Movie1";
        HttpConnection.setMovieName(movieName);

        String actualMovieName = HttpConnection.getMovieName();
        assertEquals(movieName, actualMovieName);
    }

    @Test
    public void testSetMovieName() {
        String movieName = "Movie1";
        HttpConnection.setMovieName(movieName);

        String actualMovieName = HttpConnection.getMovieName();
        assertEquals(movieName, actualMovieName);
    }
}
