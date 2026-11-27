
package edu.uj.cs372.Week6.tests;

// GitHub repo: https://github.com/Hulq98/CS-372-Java-Programming

import edu.uj.cs372.Week6.persistence.CSVRequest;
import edu.uj.cs372.Week6.model.RequestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CSVRequestTest {

    @TempDir
    Path tempDir;

    // Test 1: Check that saving creates a CSV file.
    @Test
    void saveRequest_createsFile() throws IOException {

        Path file = tempDir.resolve("requests.csv");
        CSVRequest repository = new CSVRequest(file);

        RequestData request = new RequestData(
            null, "John", "Computer Issue",
            "Computer will not start", false
        );

        repository.save(List.of(request));

        assertTrue(repository.fileExists());
        assertTrue(Files.readString(file).contains("Computer Issue"));
    }

    // Test 2: Check that saved information loads correctly.
    @Test
    void loadRequest_returnsCorrectData() throws IOException {

        Path file = tempDir.resolve("requests.csv");
        CSVRequest repository = new CSVRequest(file);

        RequestData request = new RequestData(
            null, "John", "Printer Issue",
            "Printer is offline", true
        );

        repository.save(List.of(request));

        List<RequestData> loaded = repository.load();

        assertEquals(1, loaded.size());
        assertEquals("John", loaded.get(0).name);
        assertEquals("Printer Issue", loaded.get(0).title);
        assertEquals("Printer is offline", loaded.get(0).description);
        assertTrue(loaded.get(0).completed);
    }

    // Test 3: Check that a CSV with no requests loads correctly.
    @Test
    void loadEmptyRequests_returnsEmptyList() throws IOException {

        Path file = tempDir.resolve("requests.csv");
        CSVRequest repository = new CSVRequest(file);

        repository.save(List.of());

        assertTrue(repository.load().isEmpty());
    }

    // Test 4: Check that invalid information is rejected.
    @Test
    void loadInvalidData_throwsException() throws IOException {

        Path file = tempDir.resolve("requests.csv");
        CSVRequest repository = new CSVRequest(file);

        Files.writeString(file,
            "name,title,description,completed\n" +
            "John,Printer Issue,Offline,false\n" +
            "John,Computer Issue,Offline,invalid\n"
        );

        IOException error = assertThrows(
            IOException.class, () -> repository.load()
        );

        assertTrue(error.getMessage().contains("invalid rows"));
    }

    // Test 5: Check that commas and quotes are preserved.
    @Test
    void loadSpecialCharacters_preservesData() throws IOException {

        Path file = tempDir.resolve("requests.csv");
        CSVRequest repository = new CSVRequest(file);

        RequestData request = new RequestData(
            null, "John", "Computer, Printer",
            "Error says \"Offline\"", false
        );

        repository.save(List.of(request));

        List<RequestData> loaded = repository.load();

        assertEquals("Computer, Printer", loaded.get(0).title);
        assertEquals("Error says \"Offline\"",
            loaded.get(0).description);
        assertFalse(loaded.get(0).completed);
    }
}
