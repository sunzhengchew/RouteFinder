package com.example.routefinder;

import com.example.routefinder.model.DijkstraPathfinder;
import com.example.routefinder.model.Connection;
import com.example.routefinder.model.Graph;
import com.example.routefinder.model.Station;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class MainController {
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    @FXML
    private ListView<String> avoidStationsListView;

    @FXML
    private ComboBox<String> startStationComboBox;

    @FXML
    private ComboBox<String> endStationComboBox;

    @FXML
    private AnchorPane main_page;

    @FXML
    private Spinner<Integer> penaltySpinner;

    @FXML
    private ImageView stationImage;

    @FXML
    private ListView<String> waypointsListView;

    @FXML
    private StackPane imagePane;
    @FXML
    private CheckBox dfsCheck;

    private double scale = 1.0;

    private Graph graph; // Graph instance

    public void setGraph(Graph graph) {
        this.graph = graph;
        initData(); // <--- Call after graph is set
    }

    private void initData() {
        if (graph == null) return;

        List<String> allStations = graph.getAllStationNames();
        ObservableList<String> stationItems = FXCollections.observableArrayList(allStations);

        startStationComboBox.setItems(stationItems);
        endStationComboBox.setItems(stationItems);
        waypointsListView.setItems(stationItems);
        avoidStationsListView.setItems(stationItems);

        waypointsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        avoidStationsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        penaltySpinner.setValueFactory(factory);
    }
    @FXML
    private void handleFindRoute(ActionEvent event) throws IOException {
        String start = startStationComboBox.getValue();
        String end = endStationComboBox.getValue();
        int penalty = penaltySpinner.getValue();

        // Get selected waypoints and stations to avoid
        List<String> waypoints = new ArrayList<>(waypointsListView.getSelectionModel().getSelectedItems());
        List<String> avoidStations = new ArrayList<>(avoidStationsListView.getSelectionModel().getSelectedItems());

        if (dfsCheck.isSelected()) {
            // Call DFS-based search
            handleFindAllRoutesDFS(event, start, end, avoidStations);
            return;
        }

        // Call updated pathfinder with waypoints and avoid list
        List<Station> path = DijkstraPathfinder.findShortestPath(graph, start, end, penalty, waypoints, avoidStations);

        if (path == null || path.isEmpty()) {
            // Optional: show alert if no path found
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Route Not Found");
            alert.setHeaderText("No route could be found with the given constraints.");
            alert.setContentText("Try removing some waypoints or avoided stations.");
            alert.showAndWait();
            return;
        }

        double totalDistance = calculatePathDistance(path, penalty);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("result.fxml"));
        Parent root = loader.load();

        ResultController controller = loader.getController();
        controller.initData(start, end, path, totalDistance, penalty);
        controller.setGraph(graph);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }


    @FXML
    public void initialize() {
        // Clip the image to the stackpane bounds, so it doesn’t overflow
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(imagePane.widthProperty());
        clip.heightProperty().bind(imagePane.heightProperty());
        imagePane.setClip(clip);

        StackPane.setAlignment(stationImage, Pos.CENTER);
        // DRAG LOGIC
        stationImage.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = stationImage.getTranslateX();
            translateAnchorY = stationImage.getTranslateY();
        });

        stationImage.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - mouseAnchorX;
            double offsetY = event.getSceneY() - mouseAnchorY;
            stationImage.setTranslateX(translateAnchorX + offsetX);
            stationImage.setTranslateY(translateAnchorY + offsetY);
        });
    }

    public static double calculatePathDistance(List<Station> path, int lineChangePenalty) {
        if (path == null || path.size() < 2) {
            return 0;
        }

        double totalDistance = 0.0;
        String currentLine = null;

        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);

            // Find the direct connection between from and to
            Connection connection = from.getConnections().stream()
                    .filter(c -> c.destination().equals(to))
                    .findFirst()
                    .orElse(null);

            if (connection == null) {
                System.err.println("No connection found between " + from.getName() + " and " + to.getName());
                continue;
            }

            totalDistance += connection.distance();

            // Add penalty if the line changes
            if (currentLine != null && !currentLine.equals(connection.line())) {
                totalDistance += lineChangePenalty;
            }

            currentLine = connection.line(); // update current line
        }

        return totalDistance;
    }

    private void handleFindAllRoutesDFS(ActionEvent event, String start, String end, List<String> avoidStations) throws IOException {
        if (start == null || end == null || start.equals(end)) {
            showAlert("Please select different Start and End stations.");
            return;
        }

        if (!waypointsListView.getSelectionModel().getSelectedItems().isEmpty()) {
            showAlert("Waypoints are not supported in DFS mode.");
            return;
        }

        Station startStation = graph.getStation(start);
        Station endStation = graph.getStation(end);
        Set<Station> avoidSet = avoidStations.stream()
                .map(graph::getStation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<List<Station>> allPaths = DijkstraPathfinder.findAllPathsDFS(graph, startStation, endStation, avoidSet);

        if (allPaths.isEmpty()) {
            showAlert("No paths found using DFS.");
            return;
        }

        // Optional: sort paths by length
        allPaths.sort(Comparator.comparingInt(List::size));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("result.fxml"));
        Parent root = loader.load();

        ResultController controller = loader.getController();
        controller.initMultipleRoutes(allPaths, start, end);
        controller.setGraph(graph);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleZoomIn() {
        scale = Math.min(scale * 1.1, 5.0); // limit max zoom
        stationImage.setScaleX(scale);
        stationImage.setScaleY(scale);
    }

    @FXML
    private void handleZoomOut() {
        scale = Math.max(scale / 1.1, 0.5); // limit min zoom
        stationImage.setScaleX(scale);
        stationImage.setScaleY(scale);
    }

    public void main_close() {
        System.exit(0);
    }

    public void main_minimise() {
        Stage stage = (Stage) main_page.getScene().getWindow();
        stage.setIconified(true);
    }
}

