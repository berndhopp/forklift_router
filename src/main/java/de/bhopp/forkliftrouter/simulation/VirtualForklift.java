package de.bhopp.forkliftrouter.simulation;

import de.bhopp.forkliftrouter.IForklift;
import de.bhopp.forkliftrouter.Location;

import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;

public class VirtualForklift implements IForklift<VirtualForklift> {
    @Getter @Setter private Location location;
    @Getter @Setter private double currentSpeed;
    @Getter @Setter private double orientation;
    @Getter private final Color color;

    private @Getter @Setter Supplier<Long> arrivalTimeAtCurrentRoutePointSupplier;

    @Getter private final double maximumSpeed;
    @Getter private final double acceleration;
    private final ApplicationContext applicationContext;
    private final long loadOrUnloadMillis;

    @Getter @Setter private Supplier<Location> nextStopSupplier;

    public VirtualForklift(Color color, ApplicationContext applicationContext) {
        this.color = color;
        this.maximumSpeed = 100;
        this.acceleration = 100;
        this.applicationContext = applicationContext;
        this.loadOrUnloadMillis = 120;
    }

    @Override
    public List<VirtualForklift> getOtherInstancesCloserThan30Meters() {
        return applicationContext
                .getBeansOfType(VirtualForklift.class)
                .values()
                .stream()
                .filter(i -> !i.equals(this))
                .filter(i -> i.getLocation().distanceTo(getLocation()) < 30)
                .toList();
    }

    @Override
    public float loadOrUnloadProgress() {
        if (arrivalTimeAtCurrentRoutePointSupplier.get() == -1) return -1;

        final var elapsed = currentTimeMillis() - arrivalTimeAtCurrentRoutePointSupplier.get();

        return min(1f, elapsed / (float) loadOrUnloadMillis);
    }

    @Override
    public void go() {
        if (getCurrentSpeed() == 0) {
            final var distance = getLocation().distanceTo(nextStopSupplier.get());

            final var additionalTimeFromAccelerationAndDeceleration = (getMaximumSpeed() / getAcceleration()) * 2;

            final var speed = distance / (round(distance / this.getMaximumSpeed()) + additionalTimeFromAccelerationAndDeceleration);

            setCurrentSpeed(speed);
        }
    }

    @Override
    public void stop() {
        setCurrentSpeed(0);
    }
}
