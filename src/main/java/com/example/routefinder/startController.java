package com.example.routefinder;

import com.example.routefinder.MainController;
import com.example.routefinder.model.Graph;
import com.example.routefinder.model.GraphBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class startController {
    public void switchToMain(ActionEvent event) {
        try {
            // Load graph from CSV
            Graph graph = GraphBuilder.buildGraphFromCSV();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setGraph(graph); // This now triggers initData()

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitApp() {
        System.exit(0);
    }
}