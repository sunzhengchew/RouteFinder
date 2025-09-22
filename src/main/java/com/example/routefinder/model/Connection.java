package com.example.routefinder.model;

/**
 * @param distance Placeholder for Euclidean distance
 */
public record Connection(Station destination, String line, String color, double distance) {
}

