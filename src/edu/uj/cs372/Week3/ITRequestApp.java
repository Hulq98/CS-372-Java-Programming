package edu.uj.cs372.Week3;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


//github repo: https://github.com/Hulq98/CS-372-Java-Programming
//I had issues with the coderunner extension in vscode environment interfering with runtime and causing errors. please email
//me if there are issues and I can resubmit/sit down virtually to go over what is going on. 
public class ITRequestApp extends Application {

    private ITManager manager = new ITManager();
    private ListView<ITRequest> requestList = new ListView<>();

    @Override
    public void start(Stage stage) {

        Label title = new Label("IT Request Manager");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter employee name");

        Button addButton = new Button("Add Request");
        Button closeButton = new Button("Mark Closed");

        Label message = new Label("");

        // Check if the name is empty
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (nameField.getText().trim().isEmpty()) {
                message.setText("Please enter a name.");
                event.consume();
            }
        });

        // Add a request when the button is clicked
        addButton.setOnAction(event -> {
            manager.addRequest(nameField.getText().trim());
            refreshList();
            nameField.clear();
            message.setText("Request added.");
        });

        // Close a selected request
        closeButton.setOnAction(event -> {
            ITRequest selected = requestList.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a request first.");
            } else {
                manager.closeRequest(selected);
                refreshList();
                message.setText("Request closed.");
            }
        });

        // Create the layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(title, nameField, addButton,
                requestList, closeButton, message);

        layout.setPadding(new Insets(15));

        // Create the window
        Scene scene = new Scene(layout, 400, 350);
        stage.setTitle("IT Requests");
        stage.setScene(scene);
        stage.show();
    }

    // Update the list
    private void refreshList() {
        requestList.getItems().setAll(manager.getRequests());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
