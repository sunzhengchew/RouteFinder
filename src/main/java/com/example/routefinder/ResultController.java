package com.example.routefinder;

import com.example.routefinder.model.Connection;
import com.example.routefinder.model.Graph;
import com.example.routefinder.model.Station;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResultController {
    @FXML
    private Label routeSummaryLabel;
    @FXML
    private ListView<String> routeListView;
    @FXML
    private Label totalDistanceLabel;
    @FXML
    private Label penaltyLabel;
    @FXML
    private AnchorPane result_page;
    @FXML
    private StackPane mapPane;
    private Graph graph;

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void initData(String start, String end, List<Station> path, double totalDistance, double penalty) {
        routeSummaryLabel.setText("Route from " + start + " to " + end);
        totalDistanceLabel.setText(String.format("Total distance: %.2f km", totalDistance));
        penaltyLabel.setText("Line change penalty: " + penalty);

        routeListView.getItems().clear();

        for (int i = 0; i < path.size(); i++) {
            Station station = path.get(i);
            StringBuilder step = new StringBuilder((i + 1) + ". " + station.getName());
            if (i > 0) {
                Station prev = path.get(i - 1);
                Optional<Connection> connection = prev.getConnections().stream()
                        .filter(conn -> conn.destination().equals(station))
                        .findFirst();

                connection.ifPresent(conn -> step.append(" via U").append(conn.line()).append(" line"));
            }
            routeListView.getItems().add(step.toString());
        }
        routeListView.setCellFactory(lv -> new ListCell<>() {
            private final Circle dot = new Circle(6);
            private final Label label = new Label();
            private final HBox hBox = new HBox(10, dot, label);

            {
                hBox.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);

                    String line = extractLineName(item); // helper method
                    Color color = lineColors.getOrDefault(line, Color.GRAY);
                    dot.setFill(color);

                    setGraphic(hBox);
                }
            }
        });
        mapPane.getChildren().removeIf(node -> node instanceof Line || node instanceof Text);
    }


    private final Map<String, Color> lineColors = Map.of(
            "U1", Color.RED,
            "U2", Color.PURPLE,
            "U3", Color.ORANGE,
            "U4", Color.GREEN,
            "U6", Color.rgb(174,91,91)
    );

    private String extractLineName(String text) {
        int viaIndex = text.indexOf("via ");
        if (viaIndex != -1) {
            int lineStart = viaIndex + 4;
            int lineEnd = text.indexOf(" ", lineStart);
            if (lineEnd == -1) lineEnd = text.length(); // fallback to end
            String line = text.substring(lineStart, lineEnd);
            System.out.println("Extracted line: " + line);
            return line.trim(); // Ensure no extra spaces
        }
        return ""; // default if not found
    }

    public void initMultipleRoutes(List<List<Station>> paths, String start, String end) {
        routeSummaryLabel.setText("All DFS routes from " + start + " to " + end);
        totalDistanceLabel.setText("");
        penaltyLabel.setText("");

        routeListView.getItems().clear();

        int max = Math.min(5, paths.size()); // show only top 5
        for (int i = 0; i < max; i++) {
            List<Station> path = paths.get(i);
            StringBuilder sb = new StringBuilder("Route " + (i + 1) + ": ");
            for (Station s : path) {
                sb.append(s.getName()).append(" → ");
            }
            sb.setLength(sb.length() - 3); // remove last arrow
            routeListView.getItems().add(sb.toString());
        }
    }




    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = loader.load();

            // Get controller and pass the graph
            MainController mainController = loader.getController();
            mainController.setGraph(this.graph);  // <- pass the same graph back

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void result_close() {
        System.exit(0);
    }

    public void result_minimise() {
        Stage stage = (Stage) result_page.getScene().getWindow();
        stage.setIconified(true);
    }


}
