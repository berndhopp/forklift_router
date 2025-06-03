package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.Location;
import de.bhopp.forkliftrouter.simulation.VirtualForkliftController;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ForkliftControllerRed extends VirtualForkliftController {
  public ForkliftControllerRed(List<Location> routePoint, ForkliftRed forklift) {
    super(
        List.of(routePoint.get(0), routePoint.get(3), routePoint.get(1), routePoint.get(4)),
        routePoint.get(3),
        forklift);
  }
}
