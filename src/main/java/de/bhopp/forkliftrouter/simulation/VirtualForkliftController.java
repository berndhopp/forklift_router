package de.bhopp.forkliftrouter.simulation;

import static java.lang.System.currentTimeMillis;

import de.bhopp.forkliftrouter.ForkliftController;
import de.bhopp.forkliftrouter.Location;
import java.util.List;
import lombok.Getter;

public abstract class VirtualForkliftController extends ForkliftController<VirtualForklift> {
  private @Getter long arrivalTimeAtCurrentRoutePoint;
  private int tickOutsideStationCounter = 0;
  @Getter private double averageSpeedOutsideStations = 0;

  public VirtualForkliftController(List<Location> route, Location startingPoint, VirtualForklift forklift) {
    super(route, 30, 30, forklift);
    forklift.setLocation(startingPoint);
    forklift.setNextStopSupplier(this::nextStop);
    forklift.setArrivalTimeAtCurrentRoutePointSupplier(this::getArrivalTimeAtCurrentRoutePoint);
  }

  @Override
  protected void onBeforeMoveToNextRoutePoint() {
    this.arrivalTimeAtCurrentRoutePoint = -1;
  }

  @Override
  public void tick() {
    super.tick();

    if (getForklift().getLocation().distanceTo(nextStop()) == 0 && arrivalTimeAtCurrentRoutePoint == -1) {
      arrivalTimeAtCurrentRoutePoint = currentTimeMillis();
    }

    if (getForklift().loadOrUnloadProgress() == -1) {
      ++tickOutsideStationCounter;
      averageSpeedOutsideStations = (averageSpeedOutsideStations * (tickOutsideStationCounter - 1) + getForklift().getCurrentSpeed()) / tickOutsideStationCounter;
    }
  }
}
