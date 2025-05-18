package de.bhopp.forkliftrouter.simualtion;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.System.currentTimeMillis;

import de.bhopp.forkliftrouter.domain.Location;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PhysicsEngine {
  private final List<VirtualForkliftBase> forklifts;
  private long lastUpdate = 0;

  public PhysicsEngine(java.util.List<VirtualForkliftBase> virtualForkliftList) {
    this.forklifts = virtualForkliftList;
  }

  public void update() {
    final var currentTimeMillis = currentTimeMillis();

    if (lastUpdate == 0) {
      lastUpdate = currentTimeMillis;
      return;
    }

    final double elapsedSeconds = ((double) (currentTimeMillis - lastUpdate)) / 1000;

    lastUpdate = currentTimeMillis;

    forklifts.forEach(
        v -> {
          v.update();

          final var locationBefore = v.getLocation();
          final var targetLocation = v.getRoute().get(v.getCurrentDestinationIndex());

          final var xDiffBefore = locationBefore.x() - targetLocation.x();
          final var yDiffBefore = locationBefore.y() - targetLocation.y();

          final var travelDistance = v.getCurrentSpeed() * elapsedSeconds;

          Location newLocation =
              new Location(
                  locationBefore.x() + cos(v.getOrientation()) * travelDistance,
                  locationBefore.y() + sin(v.getOrientation()) * travelDistance);

          final var xDiffAfter = newLocation.x() - targetLocation.x();
          final var yDiffAfter = newLocation.y() - targetLocation.y();

          final var steppedOverTarget =
              xDiffAfter * xDiffBefore < 0 || yDiffAfter * yDiffBefore < 0;

          if (steppedOverTarget) {
            // adjust for overstepping
            newLocation = targetLocation;
          }

          v.setLocation(newLocation);
        });
  }
}
