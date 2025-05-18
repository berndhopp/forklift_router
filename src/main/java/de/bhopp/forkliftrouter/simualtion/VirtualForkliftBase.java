package de.bhopp.forkliftrouter.simualtion;

import de.bhopp.forkliftrouter.AbstractForklift;
import de.bhopp.forkliftrouter.domain.Location;
import de.bhopp.forkliftrouter.domain.Reservation;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public abstract class VirtualForkliftBase extends AbstractForklift {
  @Getter private final Color color;
  private final ApplicationContext applicationContext;

  @Getter @Setter private Image image;

  @Getter private final Set<Reservation> myReservations = new HashSet<>();

  @Getter @Setter private Location location;

  @Getter @Setter private double maxSpeed;
  @Getter @Setter private double currentSpeed;

  @Getter @Setter private double orientation;

  public VirtualForkliftBase(
      Color color, Location location, ApplicationContext applicationContext, List<Location> route) {
    super(
        route,
        applicationContext
            .getEnvironment()
            .getRequiredProperty("de.bhopp.forkliftrouter.load_or_unload_time", Long.class),
        applicationContext
            .getEnvironment()
            .getRequiredProperty(
                "de.bhopp.forkliftrouter.min_distance_between_forklifts", Long.class));
    this.color = color;
    this.applicationContext = applicationContext;
    setMaxSpeed(
        applicationContext
            .getEnvironment()
            .getRequiredProperty("de.bhopp.forkliftrouter.max_speed", Long.class));

    this.location = location;
  }

  @Override
  protected List<VirtualForkliftBase> getOtherInstances() {
    return applicationContext.getBeansOfType(VirtualForkliftBase.class).values().stream()
        .filter(i -> !i.equals(this))
        .toList();
  }

  @Override
  protected long getTime() {
    return System.currentTimeMillis();
  }

  @Override
  protected void go() {
    if (getCurrentSpeed() == 0) {
      final var speed = calculateSpeedToCurrentTarget();
      setCurrentSpeed(speed);
    }
  }

  @Override
  protected double calculateSpeedToCurrentTarget() {
    final var distance = getLocation().distanceTo(nextStop());
    return distance / calculateTravelSeconds(distance);
  }

  @Override
  protected void stop() {
    setCurrentSpeed(0);
  }
}
