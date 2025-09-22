package com.example.routefinder.model;

import java.util.*;

public class Graph {
    private final Map<String, Station> stations = new HashMap<>();

    public Station getOrCreateStation(String name) {
        return stations.computeIfAbsent(name, Station::new);
    }

    public void addConnection(String from, String to, String line, String color, double distance) {
        Station start = getOrCreateStation(from);
        Station end = getOrCreateStation(to);

        start.addConnection(new Connection(end, line, color, distance));
        end.addConnection(new Connection(start, line, color, distance)); // bidirectional
    }

    public List<String> getAllStationNames() {
        return stations.keySet().stream()
                .sorted()
                .toList();
    }

    public Station getStation(String name) {
        return stations.get(name);
    }
    public void addStation(Station station) {
        stations.put(station.getName(), station);
    }


}

