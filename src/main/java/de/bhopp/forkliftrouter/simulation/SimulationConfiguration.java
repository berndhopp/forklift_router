package de.bhopp.forkliftrouter.simulation;

import de.bhopp.forkliftrouter.Location;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulationConfiguration {
  @Bean()
  public List<Location> route() {
    return List.of(
        new Location(400, 100),
        new Location(46, 454),
        new Location(541, 949),
        new Location(442, 397),
        new Location(46, 1054),
        new Location(200, 754),
        new Location(341, 613));
  }
}
