package com.example.routefinder.model;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private final String name;
    private final List<Connection> connections;

    public Station(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    @Override
    public String toString() {
        return name;
    }

}
