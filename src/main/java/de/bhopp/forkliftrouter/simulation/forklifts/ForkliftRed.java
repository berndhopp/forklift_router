package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.simulation.VirtualForklift;
import java.awt.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ForkliftRed extends VirtualForklift {
  public ForkliftRed(ApplicationContext applicationContext) {
    super(Color.RED, applicationContext);
  }
}
