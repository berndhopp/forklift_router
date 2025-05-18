package de.bhopp.forkliftrouter.domain;

public record Reservation(Location start, Location target, long startTime, long arrivalTime) {}
