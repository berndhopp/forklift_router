package de.bhopp.forkliftrouter.simulation;

import de.bhopp.forkliftrouter.domain.Location;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulationConfiguration {
  @Bean()
  public List<Location> route() {
    return List.of(
        new Location(700, 100),
        new Location(346, 454),
        new Location(841, 949),
        new Location(742, 397),
        new Location(346, 1054),
        new Location(500, 754),
        new Location(641, 613));
  }
}
