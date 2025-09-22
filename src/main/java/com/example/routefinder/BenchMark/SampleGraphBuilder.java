package com.example.routefinder.BenchMark;

import com.example.routefinder.model.Connection;
import com.example.routefinder.model.Graph;
import com.example.routefinder.model.Station;

public class SampleGraphBuilder {
    public static Graph buildSampleGraph() {
        Graph graph = new Graph();
        Station a = new Station("A");
        Station b = new Station("B");
        Station c = new Station("C");
        Station d = new Station("D");
        Station e = new Station("E");
        Station f = new Station("F");
        Station g = new Station("G");

        a.addConnection(new Connection(b, "U1", "forward", 1.0));
        b.addConnection(new Connection(c, "U1", "forward", 1.0));
        c.addConnection(new Connection(d, "U2", "forward", 1.0));
        d.addConnection(new Connection(g, "U2", "forward", 2.0));
        a.addConnection(new Connection(e, "U3", "forward", 2.0));
        e.addConnection(new Connection(f, "U3", "forward", 1.0));
        f.addConnection(new Connection(g, "U3", "forward", 1.5));

        graph.addStation(a);
        graph.addStation(b);
        graph.addStation(c);
        graph.addStation(d);
        graph.addStation(e);
        graph.addStation(f);
        graph.addStation(g);

        return graph;
    }
}

