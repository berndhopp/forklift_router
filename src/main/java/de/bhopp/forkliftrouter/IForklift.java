package de.bhopp.forkliftrouter;

import java.util.List;

public interface IForklift<I extends IForklift<I>> {
  //the current location of the forklift.
  Location getLocation();

  //the current speed of the forklift.
  double getCurrentSpeed();

  //the current progress of loading or unloading as a float between 0 and 1.
  float loadOrUnloadProgress();

  //the orientation of the forklift as an angle in radians.
  double getOrientation();

  //set the orientation of the forklift as an angle in radians.
  void setOrientation(double destinationAngle);

  //speed up until the maximum speed is reached.
  void go();

  //decelerate until the current speed is 0.
  void stop();

  //all other forklift instances that are closer than 30 meters.
  List<I> getOtherInstancesCloserThan30Meters();
}
