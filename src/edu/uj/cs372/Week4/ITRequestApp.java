package edu.uj.cs372.Week4;

//Import Java FX 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// New imports for file saving and loading
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


// GitHub repo: https://github.com/Hulq98/CS-372-Java-Programming


public class ITRequestApp extends Application {

    // CSV file used for storing requests
    private static final Path FILE = Path.of("requests.csv");

    private ITManager manager = new ITManager();
    private ListView<ITRequest> requestList = new ListView<>();

    // Keep the extra request information for saving
    private List<RequestData> savedRequests = new ArrayList<>();

    private Label message = new Label("");


    // Class to keep information used in the CSV file
    private static class RequestData {

        ITRequest request;
        String name;
        String title;
        String description;
        boolean completed;

        RequestData(ITRequest request, String name,
                    String title, String description,
                    boolean completed) {

            this.request = request;
            this.name = name;
            this.title = title;
            this.description = description;
            this.completed = completed;
        }
    }

    @Override
    public void start(Stage stage) {

        Label title = new Label("IT Request Manager");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter employee name");

        // New fields for title and description
        TextField titleField = new TextField();
        titleField.setPromptText("Enter request title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter request description");

        Button addButton = new Button("Add Request");
        Button closeButton = new Button("Mark Closed");

        // New Save and Load buttons
        Button saveButton = new Button("Save Requests");
        Button loadButton = new Button("Load Requests");

        // Check if the required fields are empty
        addButton.addEventFilter(ActionEvent.ACTION, event -> {

            if (nameField.getText().trim().isEmpty() ||
                titleField.getText().trim().isEmpty() ||
                descriptionField.getText().trim().isEmpty()) {

                message.setText("Please fill out all fields.");
                event.consume();
            }
        });

        // Add a request when the button is clicked
        addButton.setOnAction(event -> {

            String name = nameField.getText().trim();
            String requestTitle = titleField.getText().trim();
            String description = descriptionField.getText().trim();

            // Keep CSV records on one line
            name = name.replaceAll("\\R", " ");
            requestTitle = requestTitle.replaceAll("\\R", " ");
            description = description.replaceAll("\\R", " ");

            // Add the request using the existing manager
            manager.addRequest(name);

            List<ITRequest> requests = manager.getRequests();

            ITRequest newRequest = requests.get(requests.size() - 1);

            // Store additional information for saving
            savedRequests.add(new RequestData(
                newRequest, name, requestTitle,
                description, false
            ));

            refreshList();

            nameField.clear();
            titleField.clear();
            descriptionField.clear();

            message.setText("Request added.");
        });

        // Close a selected request
        closeButton.setOnAction(event -> {

            ITRequest selected = requestList
                .getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a request first.");
            } else {

                manager.closeRequest(selected);

                // Update the saved completion status
                for (RequestData data : savedRequests) {
                    if (data.request == selected) {
                        data.completed = true;
                        break;
                    }
                }

                refreshList();
                message.setText("Request closed.");
            }
        });

        // Save requests when clicked
        saveButton.setOnAction(event -> saveRequests());

        // Load requests when clicked
        loadButton.setOnAction(event -> loadRequests());

        // Display the extra request information in the list
        // Uses an item listener instead of updateItem()
        requestList.setCellFactory(list -> {

            ListCell<ITRequest> cell = new ListCell<>();

            cell.itemProperty().addListener((obs, oldItem, newItem) -> {

                if (newItem == null) {
                    cell.textProperty().set("");
                    return;
                }

                for (RequestData data : savedRequests) {

                    if (data.request == newItem) {

                        String status = data.completed
                                ? "Closed" : "Open";

                        cell.textProperty().set(
                            data.name + " - " + data.title
                            + " (" + status + ")"
                        );

                        return;
                    }
                }

                cell.textProperty().set(newItem.toString());
            });

            return cell;
        });

        // Create the layout
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
            title,
            nameField,
            titleField,
            descriptionField,
            addButton,
            requestList,
            closeButton,
            saveButton,
            loadButton,
            message
        );

        layout.setPadding(new Insets(15));

        // Create the window
        Scene scene = new Scene(layout, 400, 450);

        stage.setTitle("IT Requests");
        stage.setScene(scene);
        stage.show();

        // Automatically load requests when the program starts
        loadRequests();
    }

    // Update the list
    private void refreshList() {

        // Clear and reload to update the status shown
        requestList.getItems().clear();
        requestList.getItems().addAll(manager.getRequests());

        requestList.refresh();
    }

    // Save all requests to CSV
    private void saveRequests() {

        Path temporary = null;

        try {

            // Write to a temporary file first
            Path absolute = FILE.toAbsolutePath();

            temporary = Files.createTempFile(
                absolute.getParent(), "requests-", ".tmp"
            );

            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporary, StandardCharsets.UTF_8)) {

                writer.write("name,title,description,completed");
                writer.newLine();

                // Save each request as one row
                for (RequestData data : savedRequests) {

                    writer.write(
                        csv(data.name) + "," +
                        csv(data.title) + "," +
                        csv(data.description) + "," +
                        data.completed
                    );

                    writer.newLine();
                }
            }

            // Replace the old file after writing succeeds
            try {

                Files.move(temporary, absolute,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);

            } catch (AtomicMoveNotSupportedException ex) {

                Files.move(temporary, absolute,
                    StandardCopyOption.REPLACE_EXISTING);
            }

            message.setText("Requests saved successfully.");

        } catch (IOException | SecurityException ex) {

            showError("Save Error",
                "Unable to save requests: " + ex.getMessage());

        } finally {

            if (temporary != null) {

                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                }
            }
        }
    }

    // Load saved requests from CSV
    private void loadRequests() {

        // Handle a missing file
        if (Files.notExists(FILE)) {

            message.setText(
                "No saved file found. Save requests to create one."
            );
            return;
        }

        List<RequestData> loaded = new ArrayList<>();
        int skipped = 0;

        try (BufferedReader reader = Files.newBufferedReader(
                FILE, StandardCharsets.UTF_8)) {

            String line = reader.readLine();

            // Check the header
            if (!"name,title,description,completed".equals(line)) {

                showError("Load Error", "Invalid CSV file format.");
                return;
            }

            // Read each line
            while ((line = reader.readLine()) != null) {

                try {

                    List<String> fields = parseCsv(line);

                    // Validate saved information
                    if (fields.size() != 4 ||
                        fields.get(0).isBlank() ||
                        fields.get(1).isBlank() ||
                        fields.get(2).isBlank() ||
                        !(fields.get(3).equals("true") ||
                        fields.get(3).equals("false"))) {

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

            // Rebuild the manager using loaded requests
            ITManager newManager = new ITManager();

            for (RequestData data : loaded) {

                newManager.addRequest(data.name);

                List<ITRequest> requests = newManager.getRequests();

                data.request = requests.get(requests.size() - 1);

                if (data.completed) {
                    newManager.closeRequest(data.request);
                }
            }

            // Protect existing data if invalid rows were found
            if (skipped > 0) {

                showWarning("Load Warning",
                    skipped + " invalid rows were found. "
                    + "Current requests were not changed.");
                return;
            }

            // Only replace the current requests after loading
            manager = newManager;
            savedRequests = loaded;

            refreshList();

            message.setText(
                "Loaded " + loaded.size() + " requests."
            );

        } catch (IOException | SecurityException ex) {

            showError("Load Error",
                "Unable to load requests: " + ex.getMessage());
        }
    }

    // Format text so commas and quotes work in CSV
    private String csv(String value) {

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // Read CSV fields including quoted text
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

    // Show a message if an error occurs
    private void showError(String title, String text) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    // Show a warning for invalid data
    private void showWarning(String title, String text) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
