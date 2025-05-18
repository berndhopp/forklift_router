package de.bhopp.forkliftrouter;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.util.Comparator.comparingDouble;

import de.bhopp.forkliftrouter.domain.Location;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public abstract class AbstractForklift {

  private final List<Location> route;
  private final long loadOrUnloadTime;
  private final long minDistanceBetweenForklifts;
  private final long squaredMinDistanceBetweenForkLifts;
  private int currentDestinationIndex = -1;
  protected long arrivalTimeAtCurrentRoutePoint = -1;
  private boolean moveForward = true;

  public AbstractForklift(
      @NonNull List<@NonNull Location> routePoints,
      long loadOrUnloadTime,
      long minDistanceBetweenForklifts) {
    this.loadOrUnloadTime = loadOrUnloadTime;
    this.minDistanceBetweenForklifts = minDistanceBetweenForklifts;
    this.squaredMinDistanceBetweenForkLifts =
        minDistanceBetweenForklifts * minDistanceBetweenForklifts;
    if (routePoints.isEmpty()) throw new IllegalArgumentException("Route points can't be empty");
    this.route = routePoints;
  }

  protected abstract Location getLocation();

  private boolean amIClosestTo(Location location) {
    final var closestOtherDistance =
        getOtherInstances().stream()
            .map(AbstractForklift::getLocation)
            .mapToDouble(i -> i.distanceTo(location))
            .min()
            .orElseThrow();

    final var myDistance = getLocation().distanceTo(location);

    return myDistance < closestOtherDistance;
  }

  protected abstract double getMaxSpeed();

  public abstract double getCurrentSpeed();

  public float loadOrUnloadProgress() {
    if (arrivalTimeAtCurrentRoutePoint == -1) return -1;

    long elapsed = System.currentTimeMillis() - arrivalTimeAtCurrentRoutePoint;

    return elapsed / (float) loadOrUnloadTime;
  }

  protected abstract double getOrientation();

  protected abstract void setOrientation(double destinationAngle);

  protected abstract long getTime();

  protected abstract List<? extends AbstractForklift> getOtherInstances();

  protected Location nextStop() {
    return route.get(currentDestinationIndex);
  }

  protected abstract void go();

  protected abstract double calculateSpeedToCurrentTarget();

  protected abstract void stop();

  public void update() {
    if (currentDestinationIndex == -1) {
      moveToNextRoutePoint();
    }

    final var currentTimeMillis = getTime();

    final var targetLocation = route.get(currentDestinationIndex);

    final var distance = this.getLocation().distanceTo(targetLocation);

    if (distance == 0) {
      stop();

      if (arrivalTimeAtCurrentRoutePoint == -1) {
        arrivalTimeAtCurrentRoutePoint = currentTimeMillis;
      }

      final var timeSpent = currentTimeMillis - arrivalTimeAtCurrentRoutePoint;

      if (timeSpent >= loadOrUnloadTime) {
        moveToNextRoutePoint();
      }
    } else if (distance <= minDistanceBetweenForklifts) {
      if (amIClosestTo(targetLocation)) {
        go();
      } else {
        stop();
      }
    } else if (getCurrentSpeed() == 0) {
      getOtherInstances().stream()
          .filter(i -> i != this)
          .filter(i -> i.getCurrentSpeed() > 0)
          .mapToDouble(i -> i.getLocation().distanceTo(this.getLocation()))
          .min()
          .ifPresentOrElse(
              closestDistance -> {
                if (closestDistance >= minDistanceBetweenForklifts) {
                  go();
                }
              },
              this::go);
    } else {
      AbstractForklift closestOther = null;
      double closestOtherDistance = Double.MAX_VALUE;

      for (AbstractForklift otherInstance : getOtherInstances()) {
        if (otherInstance.getCurrentSpeed() == 0) {
          continue;
        }

        final var distanceToOther = otherInstance.getLocation().distanceTo(this.getLocation());

        if (distanceToOther < closestOtherDistance) {
          closestOther = otherInstance;
          closestOtherDistance = distanceToOther;
        }
      }

      if (closestOther != null) {
        if (closestOtherDistance <= minDistanceBetweenForklifts) {
          final var mySpeedTowardsOther = calculateSpeedTowards(closestOther.getLocation());

          if (mySpeedTowardsOther < 0) {
            go();
          } else {
            final var otherSpeedTowardsMe = closestOther.calculateSpeedTowards(getLocation());
            if (mySpeedTowardsOther > otherSpeedTowardsMe) {
              stop();
            }
          }
        }
      }
    }
  }

  private double calculateSpeedTowards(Location location) {
    final var myLocation = getLocation();
    final var distance = myLocation.distanceTo(location);

    final var myLocationInOneMilliSecond =
        new Location(
            myLocation.x() + cos(getOrientation()) * getCurrentSpeed() / 1000,
            myLocation.y() + sin(getOrientation()) * getCurrentSpeed() / 1000);

    final var distanceInOneSecond = myLocationInOneMilliSecond.distanceTo(location);

    return distance - distanceInOneSecond;
  }

  protected long calculateTravelSeconds(double distance) {
    // Assumption: we need 1 second to accelerate from 0 to target speed and one second to
    // decelerate from target speed to 0. So in an acceleration-cruise-deceleration-circle,
    // there will be two seconds where we cruise at half the maximum speed by average. So we
    // can add one second to the cruise time to account for acceleration/deceleration.

    return round(distance / this.getMaxSpeed()) + 1;
  }

  private void moveToNextRoutePoint() {
    if (currentDestinationIndex == -1) {
      final var closestLocation =
          route.stream()
              .filter(l -> !l.equals(getLocation()))
              .min(comparingDouble(i -> i.distanceTo(getLocation())))
              .orElseThrow();

      currentDestinationIndex = route.indexOf(closestLocation);
    } else if (currentDestinationIndex == 0) {
      currentDestinationIndex = 1;
      this.moveForward = true;
    } else if (currentDestinationIndex == route.size() - 1) {
      currentDestinationIndex = route.size() - 2;
      this.moveForward = false;
    } else if (moveForward) {
      ++currentDestinationIndex;
    } else {
      --currentDestinationIndex;
    }

    orientTowardsTarget();

    arrivalTimeAtCurrentRoutePoint = -1;

    go();
  }

  private void orientTowardsTarget() {
    final var destination = nextStop();
    final var angleToTarget = angleTo(destination);

    setOrientation(angleToTarget);
  }

  private double angleTo(Location destination) {
    final var location = getLocation();

    // see https://math.stackexchange.com/questions/1201337/finding-the-angle-between-two-points
    return atan2(destination.y() - location.y(), destination.x() - location.x());
  }
}
