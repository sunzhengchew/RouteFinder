package com.example.routefinder.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GraphBuilder {
    public static Graph buildGraphFromCSV() {
        Graph graph = new Graph();

        InputStream input = GraphBuilder.class.getResourceAsStream("/com/example/routefinder/vienna_subway.csv");
        if (input == null) {
            throw new RuntimeException("CSV file not found in resources!");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            boolean firstLine = true;  // To skip header

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;  // Skip header
                    continue;
                }

                // CSV columns: Start,Stop,Line,Color
                String[] parts = line.split(",");

                if (parts.length < 4) {
                    System.err.println("Invalid line in CSV: " + line);
                    continue;
                }

                String startStation = parts[0].trim();
                String stopStation = parts[1].trim();
                String lineName = parts[2].trim();
                String color = parts[3].trim();

                // Temporary static distance
                double distance = 1.0;

                graph.addConnection(startStation, stopStation, lineName, color, distance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }
}
