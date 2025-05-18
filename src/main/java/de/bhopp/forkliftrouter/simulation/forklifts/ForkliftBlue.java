package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.domain.Location;
import de.bhopp.forkliftrouter.simulation.SimulatedForklift;
import java.awt.*;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("singleton")
public class ForkliftBlue extends SimulatedForklift {
  public ForkliftBlue(ApplicationContext applicationContext, List<Location> routePoints) {
    super(
        Color.BLUE,
        routePoints.get(2),
        applicationContext,
        List.of(routePoints.get(0), routePoints.get(1), routePoints.get(2)));
  }
}
