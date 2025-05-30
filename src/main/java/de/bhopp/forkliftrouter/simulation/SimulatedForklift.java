package de.bhopp.forkliftrouter.simulation;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;

import de.bhopp.forkliftrouter.AbstractForklift;
import de.bhopp.forkliftrouter.domain.Location;
import java.awt.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public abstract class SimulatedForklift extends AbstractForklift {
  @Getter public final Color color;
  private final ApplicationContext applicationContext;  
  private final long loadOrUnloadMillis;
  private long arrivalTimeAtCurrentRoutePoint = -1;
  @Getter @Setter private Image image;
  @Getter @Setter private Location location;
  @Getter @Setter private double maxSpeed;
  @Getter @Setter private double currentSpeed;
  @Getter @Setter private double orientation;

  public SimulatedForklift(
      Color color, Location location, ApplicationContext applicationContext, List<Location> route) {
    super(route, 50, 50, 3, 3);

    this.loadOrUnloadMillis = 1200;
    this.color = color;
    this.applicationContext = applicationContext;

    setMaxSpeed(100);

    this.location = location;
  }

  @Override
  protected List<SimulatedForklift> getOtherInstances() {
    return applicationContext.getBeansOfType(SimulatedForklift.class).values().stream()
        .filter(i -> !i.equals(this))
        .toList();
  }

  @Override
  protected void onBeforeMoveToNextRoutePoint() {
    this.arrivalTimeAtCurrentRoutePoint = -1;
  }

  @Override
  public void tick() {
    super.tick();

    if (getLocation().distanceTo(nextStop()) == 0 && arrivalTimeAtCurrentRoutePoint == -1) {
      arrivalTimeAtCurrentRoutePoint = getTimeMillis();
    }
  }

  @Override
  protected long getTimeMillis() {
    return currentTimeMillis();
  }

  @Override
  protected void go() {
    if (getCurrentSpeed() == 0) {
      final var speed = calculateSpeedToCurrentTarget();
      setCurrentSpeed(speed);
    }
  }

  @Override
  protected void stop() {
    setCurrentSpeed(0);
  }

  @Override
  public float loadOrUnloadProgress() {
    if (arrivalTimeAtCurrentRoutePoint == -1) return -1;

    final var elapsed = currentTimeMillis() - arrivalTimeAtCurrentRoutePoint;

    return min(1f, elapsed / (float) loadOrUnloadMillis);
  }

  protected double calculateSpeedToCurrentTarget() {
    final var distance = getLocation().distanceTo(nextStop());
    // Assumption: we need 1 second to speed up from 0 to target speed and one second to
    // decelerate from target speed to 0. So in an acceleration-cruise-deceleration-circle,
    // there will be two seconds where we cruise at half the maximum speed by average. So we
    // can add one second to the cruise time to account for acceleration/deceleration.

    return distance / (round(distance / this.getMaxSpeed()) + 1);
  }
}
