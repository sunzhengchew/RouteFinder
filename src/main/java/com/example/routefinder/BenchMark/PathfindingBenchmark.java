package com.example.routefinder.BenchMark;

import com.example.routefinder.model.DijkstraPathfinder;
import com.example.routefinder.model.Graph;
import com.example.routefinder.model.Station;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class PathfindingBenchmark {

    private Graph graph;
    private String start, end;
    private List<String> waypoints;
    private List<String> avoid;

    @Setup(Level.Iteration)
    public void setup() {
        graph = SampleGraphBuilder.buildSampleGraph();
        start = "A";
        end = "G";
        waypoints = List.of("C");
        avoid = List.of("F");
    }

    @Benchmark
    public List<Station> benchmarkDijkstraWithPenalty() {
        return DijkstraPathfinder.findShortestPath(graph, start, end, 10.0, waypoints, avoid);
    }

    @Benchmark
    public List<Station> benchmarkDijkstraWithoutPenalty() {
        return DijkstraPathfinder.findShortestPath(graph, start, end, 0.0, waypoints, avoid);
    }
}
