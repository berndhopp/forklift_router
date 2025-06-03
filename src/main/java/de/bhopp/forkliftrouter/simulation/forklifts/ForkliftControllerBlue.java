package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.Location;
import de.bhopp.forkliftrouter.simulation.VirtualForkliftController;
import java.awt.*;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("singleton")
public class ForkliftControllerBlue extends VirtualForkliftController {
  public ForkliftControllerBlue(List<Location> routePoints, ForkliftBlue forklift) {
    super(
        List.of(routePoints.get(0), routePoints.get(1), routePoints.get(2)),
        routePoints.get(2),
        forklift);
  }
}
