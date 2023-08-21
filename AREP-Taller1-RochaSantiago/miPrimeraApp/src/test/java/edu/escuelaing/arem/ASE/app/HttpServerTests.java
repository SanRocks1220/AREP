package edu.escuelaing.arem.ASE.app;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class HttpServerTests {

    @Test
    public void testGetMovieDataCacheHit() throws IOException {
        String movieName = "Movie1";
        String cachedData = "{\"Title\":\"Movie Title\",\"Released\":\"2023\",\"Runtime\":\"120 min\",\"Director\":\"xName\",\"Country\":\"xCountry\",\"Poster\":\"xPoster\"}";
        
        HttpServer.cache.put(movieName, cachedData);

        String result = HttpServer.getMovieData("/getMovieData?name=" + movieName);

        String expectedResponse = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + HttpServer.htmlFormat(HttpServer.processJson(cachedData));
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testInCache() {
        HttpServer.cache.put("Movie1", "Data1");
        assertTrue(HttpServer.inCache("Movie1"));
        assertFalse(HttpServer.inCache("Movie2"));
    }

    @Test
    public void testGetFromCache() {
        HttpServer.cache.put("Movie1", "Data1");
        assertEquals("Data1", HttpServer.getFromCache("Movie1"));
        assertNull(HttpServer.getFromCache("Movie2"));
    }

    @Test
    public void testSaveInCache() {
        String movieName = "Movie1";
        String movieData = "{\"Title\":\"Movie Title\",\"Released\":\"2023\",\"Runtime\":\"120 min\",\"Director\":\"xName\",\"Country\":\"xCountry\",\"Poster\":\"xPoster\"}";

        HttpServer.saveInCache(movieName, movieData);

        assertTrue(HttpServer.cache.containsKey(movieName));
        assertTrue(HttpServer.cache.get(movieName).equals(movieData));
    }

    @Test
    public void testProcessJson() {
        String jsonString = "{\"Title\":\"Movie Title\",\"Released\":\"2023\",\"Runtime\":\"120 min\",\"Director\":\"xName\",\"Country\":\"xCountry\",\"Poster\":\"xPoster\"}";
        String expectedOutput = "Title: Movie Title\nReleased: 2023\nRuntime: 120 min\n"
                + "Director: xName\n" //
                + "Country: xCountry\n" //
                + "Poster: xPoster\n";
        assertEquals(expectedOutput, HttpServer.processJson(jsonString));
    }

    @Test
    public void testGetCaracts() {
        String[] dataParts = { "Title: Movie Title", "Released: 2023", "Runtime: 120 min" };
        assertEquals(" Movie Title", HttpServer.getCaracts(dataParts, "Title:"));
        assertEquals(" 2023", HttpServer.getCaracts(dataParts, "Released:"));
        assertEquals(" 120 min", HttpServer.getCaracts(dataParts, "Runtime:"));
        assertEquals("", HttpServer.getCaracts(dataParts, "Director:"));
    }

    @Test
    public void testHtmlFormat() {
        String rawData = "Title: Movie Title\nReleased: 2023\nRuntime: 120 min\n";
        String expectedOutput = "<ul>\n" +
                               "<li><b>Title:</b> Movie Title</li>\n" +
                               "<li><b>Released:</b> 2023</li>\n" +
                               "<li><b>Runtime:</b> 120 min</li>\n" +
                               "</ul>\n" +
                               "<center><img src= /></center>";
        assertEquals(expectedOutput, HttpServer.htmlFormat(rawData));
    }

}
