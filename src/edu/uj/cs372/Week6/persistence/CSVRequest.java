
package edu.uj.cs372.Week6.persistence;

import edu.uj.cs372.Week6.model.RequestData;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

// Handles saving and loading requests.
public class CSVRequest {

    private final Path FILE;

    // Default CSV file for the application.
    public CSVRequest() {
        this(Path.of("requests.csv"));
    }

    // Allows a different CSV file for testing.
    public CSVRequest(Path file) {
        this.FILE = file;
    }

    public boolean fileExists() {
        return Files.exists(FILE);
    }

    // Save requests to CSV.
    public void save(List<RequestData> requests) throws IOException {

        Path absolute = FILE.toAbsolutePath();

        Path temporary = Files.createTempFile(
            absolute.getParent(), "requests-", ".tmp"
        );

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporary, StandardCharsets.UTF_8)) {

                writer.write("name,title,description,completed");
                writer.newLine();

                for (RequestData data : requests) {
                    writer.write(
                        csv(data.name) + "," +
                        csv(data.title) + "," +
                        csv(data.description) + "," +
                        data.completed
                    );
                    writer.newLine();
                }
            }

            try {
                Files.move(temporary, absolute,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);

            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporary, absolute,
                    StandardCopyOption.REPLACE_EXISTING);
            }

        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    // Load and validate CSV data.
    public List<RequestData> load() throws IOException {

        List<RequestData> loaded = new ArrayList<>();
        int skipped = 0;

        try (BufferedReader reader = Files.newBufferedReader(
                FILE, StandardCharsets.UTF_8)) {

            String line = reader.readLine();

            if (!"name,title,description,completed".equals(line)) {
                throw new IOException("Invalid CSV file format.");
            }

            while ((line = reader.readLine()) != null) {

                try {
                    List<String> fields = parseCsv(line);

                    if (!isValidRequest(fields)) {
                        skipped++;
                        continue;
                    }

                    loaded.add(new RequestData(
                        null,
                        fields.get(0),
                        fields.get(1),
                        fields.get(2),
                        Boolean.parseBoolean(fields.get(3))
                    ));

                } catch (IllegalArgumentException ex) {
                    skipped++;
                }
            }
        }

        // Do not return partial data if errors were found.
        if (skipped > 0) {
            throw new IOException(
                skipped + " invalid rows were found. " +
                "Current requests were not changed."
            );
        }

        return loaded;
    }

    // Validate the information from a CSV row.
    private boolean isValidRequest(List<String> fields) {

        return fields.size() == 4 &&
               !fields.get(0).isBlank() &&
               !fields.get(1).isBlank() &&
               !fields.get(2).isBlank() &&
               (fields.get(3).equals("true") ||
                fields.get(3).equals("false"));
    }

    // Format strings for CSV.
    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // Read CSV fields including commas and quotes.
    private List<String> parseCsv(String line) {

        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();

        boolean quoted = false;
        boolean started = false;
        boolean closedQuote = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (quoted) {
                if (c == '"') {

                    if (i + 1 < line.length() &&
                        line.charAt(i + 1) == '"') {

                        field.append('"');
                        i++;

                    } else {
                        quoted = false;
                        closedQuote = true;
                    }

                } else {
                    field.append(c);
                }

            } else if (c == ',') {
                fields.add(field.toString());
                field.setLength(0);
                started = false;
                closedQuote = false;

            } else if (c == '"' && !started && !closedQuote) {
                quoted = true;
                started = true;

            } else if (c == '"' || closedQuote) {
                throw new IllegalArgumentException("Invalid CSV");

            } else {
                field.append(c);
                started = true;
            }
        }

        if (quoted) {
            throw new IllegalArgumentException("Unclosed CSV field");
        }

        fields.add(field.toString());
        return fields;
    }
}
