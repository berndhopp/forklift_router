package de.bhopp.forkliftrouter;

import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

public record Location(double x, double y) {

  public double distanceTo(Location other) {
    final var xDiff = other.x() - this.x();
    final var yDiff = other.y() - this.y();
    return sqrt(xDiff * xDiff + yDiff * yDiff);
  }

  public double angleTo(Location other) {
    // see https://math.stackexchange.com/questions/1201337/finding-the-angle-between-two-points
    return atan2(other.y() - this.y(), other.x() - this.x());
  }
}
