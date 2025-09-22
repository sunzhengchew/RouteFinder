package com.example.routefinder.model;

import java.util.*;
import java.util.stream.Collectors;

public class DijkstraPathfinder {

    public static List<Station> findShortestPath(Graph graph, String startName, String endName, double lineChangePenalty,
                                                 List<String> waypoints, List<String> avoidStations) {
        Station start = graph.getStation(startName);
        Station end = graph.getStation(endName);
        if (start == null || end == null) return null;

        // Convert avoid names to Station objects
        Set<Station> avoid = avoidStations.stream()
                .map(graph::getStation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Build the full waypoint route: start -> waypoint1 -> ... -> waypointN -> end
        List<String> fullRoute = new ArrayList<>();
        fullRoute.add(startName);
        fullRoute.addAll(waypoints);
        fullRoute.add(endName);

        List<Station> totalPath = new ArrayList<>();
        for (int i = 0; i < fullRoute.size() - 1; i++) {
            Station segmentStart = graph.getStation(fullRoute.get(i));
            Station segmentEnd = graph.getStation(fullRoute.get(i + 1));
            if (segmentStart == null || segmentEnd == null) return null;

            // Find path between two segments
            List<Station> segmentPath = findSegmentPath(graph, segmentStart, segmentEnd, lineChangePenalty, avoid);
            if (segmentPath == null) return null;

            if (!totalPath.isEmpty()) segmentPath.remove(0); // avoid repeating node
            totalPath.addAll(segmentPath);
        }

        return totalPath;
    }

    // Internal method for Dijkstra between 2 points with line change penalty & station avoidance
    private static List<Station> findSegmentPath(Graph graph, Station start, Station end, double lineChangePenalty, Set<Station> avoid) {
        PriorityQueue<PathNode> queue = new PriorityQueue<>();
        Map<Station, Double> visited = new HashMap<>();
        queue.add(new PathNode(start, 0, new ArrayList<>(List.of(start)), null));

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();

            if (current.station.equals(end)) {
                return current.path;
            }

            if (visited.containsKey(current.station) && visited.get(current.station) <= current.cost) continue;
            visited.put(current.station, current.cost);

            for (Connection connection : current.station.getConnections()) {
                Station neighbor = connection.destination();
                if (avoid.contains(neighbor)) continue;

                double cost = connection.distance();
                if (current.currentLine != null && !current.currentLine.equals(connection.line())) {
                    cost += lineChangePenalty;
                }

                double newCost = current.cost + cost;
                List<Station> newPath = new ArrayList<>(current.path);
                newPath.add(neighbor);
                queue.add(new PathNode(neighbor, newCost, newPath, connection.line()));
            }
        }

        return null;
    }
    public static List<List<Station>> findAllPathsDFS(Graph graph, Station start, Station end, Set<Station> avoid) {
        List<List<Station>> allPaths = new ArrayList<>();
        List<Station> currentPath = new ArrayList<>();
        Set<Station> visited = new HashSet<>();
        dfsHelper(start, end, avoid, visited, currentPath, allPaths);
        return allPaths;
    }

    private static void dfsHelper(Station current, Station end, Set<Station> avoid,
                                  Set<Station> visited, List<Station> currentPath,
                                  List<List<Station>> allPaths) {
        if (avoid.contains(current)) return;

        visited.add(current);
        currentPath.add(current);

        if (current.equals(end)) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            for (Connection connection : current.getConnections()) {
                Station neighbor = connection.destination();
                if (!visited.contains(neighbor) && !avoid.contains(neighbor)) {
                    dfsHelper(neighbor, end, avoid, visited, currentPath, allPaths);
                }
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }

}

