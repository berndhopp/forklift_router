package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.domain.Location;
import de.bhopp.forkliftrouter.simulation.SimulatedForklift;
import java.awt.*;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ForkliftRed extends SimulatedForklift {
  public ForkliftRed(ApplicationContext applicationContext, List<Location> routePoint) {
    super(
        Color.RED,
        routePoint.get(3),
        applicationContext,
        List.of(routePoint.get(0), routePoint.get(3), routePoint.get(1), routePoint.get(4)));
  }
}
