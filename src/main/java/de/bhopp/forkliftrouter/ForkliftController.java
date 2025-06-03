package de.bhopp.forkliftrouter;

import static java.lang.Math.cos;
import static java.util.Comparator.comparingDouble;

import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class ForkliftController<FORKLIFT extends IForklift<FORKLIFT>> {

  private final List<Location> route;
  private final long minDistanceBetweenForklifts;
  private final long stationEntryDistance;
  private final FORKLIFT forklift;
  private int currentDestinationIndex = -1;
  private boolean moveForward = true;

  public ForkliftController(
      @NonNull List<@NonNull Location> routePoints,
      long minDistanceBetweenForklifts,
      long stationEntryDistance,
      @NonNull FORKLIFT forklift) {
    this.forklift = forklift;
    if (routePoints.isEmpty()) throw new IllegalArgumentException("Route points can't be empty");
    this.stationEntryDistance = stationEntryDistance;
    this.minDistanceBetweenForklifts = minDistanceBetweenForklifts;
    this.route = routePoints;
  }

  protected Location nextStop() {
    return getCurrentTarget();
  }

  public void tick() {
    if (currentDestinationIndex == -1) {
      moveToNextRoutePoint();
    }

    final var targetLocation = getCurrentTarget();
    final var distance = forklift.getLocation().distanceTo(targetLocation);

    if (distance == 0) {
      handleLoadingOrUnloading();
    } else if (distance <= stationEntryDistance) {
      handleStationEntry();
    } else if (forklift.getCurrentSpeed() != 0) {
      handleMoving();
    } else {
      handleNotMoving();
    }
  }

  private void handleMoving() {
    record OtherForkliftAndDistance(IForklift<?> forklift, double distance) {}

    forklift.getOtherInstancesCloserThan30Meters().stream()
        .filter(other -> other.getCurrentSpeed() > 0)
        .map(
            other ->
                new OtherForkliftAndDistance(
                    other,
                    other.getLocation().distanceTo(this.forklift.getLocation())))
        .min(comparingDouble(OtherForkliftAndDistance::distance))
        .filter(
            closestOtherAndDistance ->
                closestOtherAndDistance.distance <= minDistanceBetweenForklifts)
        .ifPresent(
            closestOther -> {
              final var mySpeedTowardsOther =
                  calculateSpeedTowards(forklift, closestOther.forklift.getLocation());

              if (mySpeedTowardsOther < 0) {
                forklift.go();
              } else {
                final var otherSpeedTowardsMe =
                    calculateSpeedTowards(closestOther.forklift(), forklift.getLocation());
                if (mySpeedTowardsOther > otherSpeedTowardsMe) {
                  forklift.stop();
                }
              }
            });
  }

  private void handleNotMoving() {
    forklift.getOtherInstancesCloserThan30Meters().stream()
        .filter(i -> i.getCurrentSpeed() > 0)
        .mapToDouble(i -> i.getLocation().distanceTo(this.forklift.getLocation()))
        .min()
        .ifPresentOrElse(
            closestDistance -> {
              if (closestDistance >= minDistanceBetweenForklifts) {
                forklift.go();
              }
            },
            forklift::go);
  }

  private void handleStationEntry() {
    final var meIsTheClosestForkliftToTargetStation =
            forklift.getOtherInstancesCloserThan30Meters().stream()
            .noneMatch(
                other ->
                        other.getLocation().distanceTo(getCurrentTarget())
                        <= forklift.getLocation().distanceTo(getCurrentTarget()));

    if (meIsTheClosestForkliftToTargetStation) {
      forklift.go();
    } else {
      forklift.stop();
    }
  }

  public Location getCurrentTarget() {
    return route.get(currentDestinationIndex);
  }

  private void handleLoadingOrUnloading() {
    if (forklift.loadOrUnloadProgress() == 1) {
      moveToNextRoutePoint();
    } else {
      forklift.stop();
    }
  }

  private double calculateSpeedTowards(IForklift<?> forklift, Location target) {
    final var angleDifference = forklift.getOrientation() - forklift.getLocation().angleTo(target);
    final var speedProjection = cos(angleDifference);

    return forklift.getCurrentSpeed() * speedProjection;
  }

  protected void onBeforeMoveToNextRoutePoint() {}

  private void moveToNextRoutePoint() {
    onBeforeMoveToNextRoutePoint();

    if (currentDestinationIndex == -1) {
      final var closestLocation =
          route.stream()
              .filter(l -> !l.equals(forklift.getLocation()))
              .min(comparingDouble(i -> i.distanceTo(forklift.getLocation())))
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

    forklift.go();
  }

  private void orientTowardsTarget() {
    final var destination = nextStop();
    final var angleToDestination = forklift.getLocation().angleTo(destination);

    forklift.setOrientation(angleToDestination);
  }
}
