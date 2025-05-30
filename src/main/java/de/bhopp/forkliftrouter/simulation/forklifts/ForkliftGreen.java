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
public class ForkliftGreen extends SimulatedForklift {
  public ForkliftGreen(ApplicationContext applicationContext, List<Location> routePoint) {
    super(
        Color.GREEN,
        routePoint.get(5),
        applicationContext,
        List.of(routePoint.get(5), routePoint.get(6)));
  }
}
