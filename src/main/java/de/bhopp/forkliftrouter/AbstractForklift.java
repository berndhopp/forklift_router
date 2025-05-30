package de.bhopp.forkliftrouter;

import static java.lang.Math.cos;
import static java.util.Comparator.comparingDouble;

import de.bhopp.forkliftrouter.domain.Location;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public abstract class AbstractForklift {

  private final List<Location> route;
  private final long minDistanceBetweenForklifts;
  private final long stationEntryDistance;
  private int currentDestinationIndex = -1;
  private boolean moveForward = true;
  protected final double maxSpeed;
  protected final double acceleration;

  public AbstractForklift(
      @NonNull List<@NonNull Location> routePoints,
      long minDistanceBetweenForklifts,
      long stationEntryDistance,
      double maxSpeed,
      double acceleration) {
    if (routePoints.isEmpty()) throw new IllegalArgumentException("Route points can't be empty");
    this.stationEntryDistance = stationEntryDistance;
    this.minDistanceBetweenForklifts = minDistanceBetweenForklifts;
    this.maxSpeed = maxSpeed;
    this.acceleration = acceleration;
    this.route = routePoints;
  }

  public abstract Location getLocation();

  public abstract double getCurrentSpeed();

  public abstract float loadOrUnloadProgress();

  protected abstract double getOrientation();

  protected abstract void setOrientation(double destinationAngle);

  protected abstract long getTimeMillis();

  protected abstract void go();

  protected abstract void stop();

  protected abstract List<? extends AbstractForklift> getOtherInstances();

  protected Location nextStop() {
    return getCurrentTarget();
  }

  public void tick() {
    if (currentDestinationIndex == -1) {
      moveToNextRoutePoint();
    }

    final var targetLocation = getCurrentTarget();
    final var distance = this.getLocation().distanceTo(targetLocation);

    if (distance == 0) {
      handleLoadingOrUnloading();
    } else if (distance <= stationEntryDistance) {
      handleStationEntry();
    } else if (getCurrentSpeed() != 0) {
      handleMoving();
    } else {
      handleNotMoving();
    }
  }

  private void handleMoving() {
    record OtherForkliftAndDistance(AbstractForklift forklift, double distance) {}

    getOtherInstances().stream()
        .filter(other -> other.getCurrentSpeed() > 0)
        .map(
            other ->
                new OtherForkliftAndDistance(
                    other, other.getLocation().distanceTo(this.getLocation())))
        .min(comparingDouble(OtherForkliftAndDistance::distance))
        .filter(
            closestOtherAndDistance ->
                closestOtherAndDistance.distance <= minDistanceBetweenForklifts)
        .ifPresent(
            closestOther -> {
              final var mySpeedTowardsOther =
                  calculateSpeedTowards(closestOther.forklift.getLocation());

              if (mySpeedTowardsOther < 0) {
                go();
              } else {
                final var otherSpeedTowardsMe =
                    closestOther.forklift.calculateSpeedTowards(getLocation());
                if (mySpeedTowardsOther > otherSpeedTowardsMe) {
                  stop();
                }
              }
            });
  }

  private void handleNotMoving() {
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
  }

  private void handleStationEntry() {
    final var meIsTheClosestForkliftToTargetStation = getOtherInstances()
            .stream()
            .noneMatch(other -> other.getLocation().distanceTo(getCurrentTarget()) <= getLocation().distanceTo(getCurrentTarget()));

    if (meIsTheClosestForkliftToTargetStation) {
      go();
    } else {
      stop();
    }
  }

  public Location getCurrentTarget() {
    return route.get(currentDestinationIndex);
  }

  private void handleLoadingOrUnloading() {
    if (loadOrUnloadProgress() == 1) {
      moveToNextRoutePoint();
    } else {
      stop();
    }
  }

  private double calculateSpeedTowards(Location target) {
    final var angleDifference = getOrientation() - getLocation().angleTo(target);
    final var speedProjection = cos(angleDifference);

    return getCurrentSpeed() * speedProjection;
  }

  protected void onBeforeMoveToNextRoutePoint() {}

  private void moveToNextRoutePoint() {
    onBeforeMoveToNextRoutePoint();

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

    go();
  }

  private void orientTowardsTarget() {
    final var destination = nextStop();
    final var angleToDestination = getLocation().angleTo(destination);

    setOrientation(angleToDestination);
  }
}
