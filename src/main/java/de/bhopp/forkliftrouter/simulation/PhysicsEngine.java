package de.bhopp.forkliftrouter.simulation;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.System.currentTimeMillis;

import de.bhopp.forkliftrouter.Location;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PhysicsEngine {
  private final List<VirtualForkliftController> forklifts;
  private long lastUpdate = 0;

  public PhysicsEngine(java.util.List<VirtualForkliftController> virtualForkliftList) {
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
          v.tick();

          final var locationBefore = v.getForklift().getLocation();
          final var targetLocation = v.getCurrentTarget();

          final var xDiffBefore = locationBefore.x() - targetLocation.x();
          final var yDiffBefore = locationBefore.y() - targetLocation.y();

          final var travelDistance = v.getForklift().getCurrentSpeed() * elapsedSeconds;

          var newLocation =
              new Location(
                  locationBefore.x() + cos(v.getForklift().getOrientation()) * travelDistance,
                  locationBefore.y() + sin(v.getForklift().getOrientation()) * travelDistance);

          final var xDiffAfter = newLocation.x() - targetLocation.x();
          final var yDiffAfter = newLocation.y() - targetLocation.y();

          final var steppedOverTarget =
              xDiffAfter * xDiffBefore < 0 || yDiffAfter * yDiffBefore < 0;

          if (steppedOverTarget) {
            // adjust for overstepping
            newLocation = targetLocation;
          }

          v.getForklift().setLocation(newLocation);
        });
  }
}
