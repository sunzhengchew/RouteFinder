package com.example.routefinder.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraPathfinderTest {

    @Test
    public void testFindShortestPath_SimplePath_NoLineChangePenalty() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);
        graph.addConnection("B", "C", "Red", "Red", 1);
        graph.addConnection("C", "D", "Red", "Red", 1);

        List<Station> path = DijkstraPathfinder.findShortestPath(
                graph, "A", "D", 0, List.of(), List.of()
        );

        assertNotNull(path);
        assertEquals(List.of("A", "B", "C", "D"), path.stream().map(Station::getName).toList());
    }

    @Test
    public void testFindShortestPath_WithLineChangePenalty() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);
        graph.addConnection("B", "C", "Blue", "Blue", 1);
        graph.addConnection("C", "D", "Blue", "Blue", 1);
        graph.addConnection("A", "D", "Green", "Green", 10); // single-line route

        double lineChangePenalty = 10;

        List<Station> path = DijkstraPathfinder.findShortestPath(
                graph, "A", "D", lineChangePenalty, List.of(), List.of()
        );

        assertNotNull(path);
        assertEquals(List.of("A", "D"), path.stream().map(Station::getName).toList());
    }

    @Test
    public void testFindShortestPath_WithWaypoint() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);
        graph.addConnection("B", "C", "Red", "Red", 1);
        graph.addConnection("C", "D", "Red", "Red", 1);

        List<Station> path = DijkstraPathfinder.findShortestPath(
                graph, "A", "D", 0, List.of("B"), List.of()
        );

        assertNotNull(path);
        assertEquals(List.of("A", "B", "C", "D"), path.stream().map(Station::getName).toList());
    }

    @Test
    public void testFindShortestPath_WithAvoidStation() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);
        graph.addConnection("B", "C", "Red", "Red", 1);
        graph.addConnection("C", "D", "Red", "Red", 1);
        graph.addConnection("A", "D", "Blue", "Blue", 5); // alternate path avoiding B

        List<Station> path = DijkstraPathfinder.findShortestPath(
                graph, "A", "D", 0, List.of(), List.of("B")
        );

        assertNotNull(path);
        assertEquals(List.of("A", "D"), path.stream().map(Station::getName).toList());
    }

    @Test
    public void testFindShortestPath_NoPathExists() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);
        graph.addConnection("C", "D", "Blue", "Blue", 1);

        List<Station> path = DijkstraPathfinder.findShortestPath(
                graph, "A", "D", 0, List.of(), List.of()
        );

        assertNull(path);
    }

    @Test
    public void testFindShortestPath_StartEqualsEnd() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);

        List<Station> path = DijkstraPathfinder.findShortestPath(
                graph, "A", "A", 0, List.of(), List.of()
        );

        assertNotNull(path);
        assertEquals(List.of("A"), path.stream().map(Station::getName).toList());
    }

    @Test
    public void testFindShortestPath_NullStation() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);

        assertNull(DijkstraPathfinder.findShortestPath(graph, "X", "B", 0, List.of(), List.of()));
        assertNull(DijkstraPathfinder.findShortestPath(graph, "A", "Z", 0, List.of(), List.of()));
    }

    @Test
    public void testFindShortestPath_WaypointAvoidConflict() {
        Graph graph = new Graph();

        graph.addConnection("A", "B", "Red", "Red", 1);
        graph.addConnection("B", "C", "Red", "Red", 1);
        graph.addConnection("C", "D", "Red", "Red", 1);

        // B is required but also avoided → should fail
        List<Station> path = DijkstraPathfinder.findShortestPath(
                graph, "A", "D", 0, List.of("B"), List.of("B")
        );

        assertNull(path);
    }
}

