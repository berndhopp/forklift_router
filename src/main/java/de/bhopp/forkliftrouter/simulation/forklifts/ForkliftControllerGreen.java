package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.Location;
import de.bhopp.forkliftrouter.simulation.VirtualForkliftController;
import java.awt.*;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ForkliftControllerGreen extends VirtualForkliftController {
  public ForkliftControllerGreen(List<Location> routePoint, ForkliftGreen forklift) {
    super(List.of(routePoint.get(5), routePoint.get(6)), routePoint.get(5), forklift);
  }
}
