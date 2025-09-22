package com.example.routefinder.model;

import java.util.List;

public class PathNode implements Comparable<PathNode> {
    Station station;
    double cost;
    List<Station> path;
    String currentLine; // for tracking line changes

    public PathNode(Station station, double cost, List<Station> path, String currentLine) {
        this.station = station;
        this.cost = cost;
        this.path = path;
        this.currentLine = currentLine;
    }

    @Override
    public int compareTo(PathNode other) {
        return Double.compare(this.cost, other.cost);
    }
}
