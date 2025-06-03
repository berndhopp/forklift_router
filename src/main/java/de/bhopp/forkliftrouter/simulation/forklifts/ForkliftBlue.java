package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.simulation.VirtualForklift;
import java.awt.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ForkliftBlue extends VirtualForklift {
  public ForkliftBlue(ApplicationContext applicationContext) {
    super(Color.BLUE, applicationContext);
  }
}
