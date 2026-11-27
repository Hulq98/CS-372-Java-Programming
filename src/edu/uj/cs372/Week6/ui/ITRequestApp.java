
package edu.uj.cs372.Week6.ui;

// GitHub repo: https://github.com/Hulq98/CS-372-Java-Programming

import edu.uj.cs372.Week6.model.ITRequest;
import edu.uj.cs372.Week6.model.RequestData;
import edu.uj.cs372.Week6.service.ITManager;
import edu.uj.cs372.Week6.persistence.CSVRequest;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ITRequestApp extends Application {

    private ITManager manager = new ITManager();

    private CSVRequest repository = new CSVRequest();

    private ListView<ITRequest> requestList = new ListView<>();

    private List<RequestData> savedRequests = new ArrayList<>();

    private Label message = new Label("");

    @Override
    public void start(Stage stage) {

        Label title = new Label("IT Request Manager");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter employee name");

        TextField titleField = new TextField();
        titleField.setPromptText("Enter request title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter request description");

        Button addButton = new Button("Add Request");
        Button closeButton = new Button("Mark Closed");

        Button saveButton = new Button("Save Requests");
        Button loadButton = new Button("Load Requests");

        // Validate fields before adding a request.
        addButton.addEventFilter(ActionEvent.ACTION, event -> {

            if (nameField.getText().trim().isEmpty() ||
                titleField.getText().trim().isEmpty() ||
                descriptionField.getText().trim().isEmpty()) {

                message.setText("Please fill out all fields.");
                event.consume();
            }
        });

        // Add a request.
        addButton.setOnAction(event -> {

            String name = nameField.getText().trim();
            String requestTitle = titleField.getText().trim();
            String description = descriptionField.getText().trim();

            name = name.replaceAll("\\R", " ");
            requestTitle = requestTitle.replaceAll("\\R", " ");
            description = description.replaceAll("\\R", " ");

            // Week 5 fix: directly return the new object.
            ITRequest newRequest = manager.addRequest(name);

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

        // Close the selected request.
        closeButton.setOnAction(event -> {

            ITRequest selected = requestList
                .getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a request first.");

            } else {
                manager.closeRequest(selected);

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

        // Save and Load button events.
        saveButton.setOnAction(event -> saveRequests());
        loadButton.setOnAction(event -> loadRequests());

        // Display request information in the list.
        requestList.setCellFactory(list -> new ListCell<ITRequest>() {

            @Override
            protected void updateItem(ITRequest item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                for (RequestData data : savedRequests) {
                    if (data.request == item) {

                        String status = data.completed
                            ? "Closed" : "Open";

                        setText(
                            data.name + " - " + data.title +
                            " (" + status + ")"
                        );
                        return;
                    }
                }

                setText(item.toString());
            }
        });

        // Create the layout.
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

        Scene scene = new Scene(layout, 400, 450);

        stage.setTitle("IT Requests");
        stage.setScene(scene);
        stage.show();

        // Load saved requests when the app opens.
        loadRequests();
    }

    // Refresh the request list.
    private void refreshList() {
        requestList.getItems().clear();
        requestList.getItems().addAll(manager.getRequests());
        requestList.refresh();
    }

    // Save requests using the repository.
    private void saveRequests() {

        try {
            repository.save(savedRequests);
            message.setText("Requests saved successfully.");

        } catch (IOException | SecurityException ex) {
            showError("Save Error",
                "Unable to save requests: " + ex.getMessage());
        }
    }

    // Load requests using the repository.
    private void loadRequests() {

        if (!repository.fileExists()) {
            message.setText(
                "No saved file found. Save requests to create one."
            );
            return;
        }

        try {
            // Read and validate data first.
            List<RequestData> loaded = repository.load();

            ITManager newManager = new ITManager();

            for (RequestData data : loaded) {

                // Week 5 fix: no HashMap order assumption.
                data.request = newManager.addRequest(data.name);

                if (data.completed) {
                    newManager.closeRequest(data.request);
                }
            }

            // Replace existing data only after successful load.
            manager = newManager;
            savedRequests = loaded;

            refreshList();

            message.setText(
                "Loaded " + loaded.size() + " requests."
            );

        } catch (IOException ex) {
            if (ex.getMessage() != null &&
                ex.getMessage().contains("invalid rows")) {

                showWarning("Load Warning", ex.getMessage());

            } else {
                showError("Load Error",
                    "Unable to load requests: " + ex.getMessage());
            }

        } catch (SecurityException ex) {
            showError("Load Error",
                "Unable to load requests: " + ex.getMessage());
        }
    }

    // Show an error message.
    private void showError(String title, String text) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    // Show a warning message.
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
