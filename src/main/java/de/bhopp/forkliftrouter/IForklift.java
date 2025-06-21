package de.bhopp.forkliftrouter;

import java.util.List;

/**
 * An abstraction layer intended to translate between the forklift hardware and
 * the {@link ForkliftController}
 * */
public interface IForklift<I extends IForklift<I>> {
  /** returns the current {@link Location} of the forklift. */
  Location getLocation();

  /** returns the current speed of the forklift. */
  double getCurrentSpeed();

  /**
   * Returns the current progress of loading or unloading in the interval
   * between 0 (inclusive) and 1 (inclusive).
   * If the forklift is not loading or unloading, -1 is returned.
   * */
  float loadOrUnloadProgress();

  /** returns the orientation of the forklift as an angle in radians*/
  double getOrientation();

  /**
   * sets the orientation that the forklift should orient towards
   * @param destinationAngle the angle in radians
   * */
  void setOrientation(double destinationAngle);

  /** speed up until the maximum speed is reached.*/
  void go();

  /** decelerate until the current speed is 0.*/
  void stop();

  /**
   * returns a list of all other forklift instances within the given proximity
   * @param proximity the maximum proximity in meters that any instance in the returned
   *                  list may have in relation to the caller
   * @throws IllegalArgumentException if the given proximity is smaller than or equal to zero
   * */
  List<I> getOtherInstances(double proximity);
}
