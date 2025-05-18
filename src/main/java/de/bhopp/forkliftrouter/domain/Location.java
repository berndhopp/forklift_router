package de.bhopp.forkliftrouter.domain;

import static java.lang.Math.sqrt;

public record Location(double x, double y) {

  /**
   * Uses Pythagoras' theorem to calculate the distance between the current start and the given
   * start
   *
   * @param other the start to calculate the distance to
   * @return the calculated distance
   */
  public double distanceTo(Location other) {
    double xDiff = other.x() - this.x();
    double yDiff = other.y() - this.y();
    return sqrt(xDiff * xDiff + yDiff * yDiff);
  }
}
