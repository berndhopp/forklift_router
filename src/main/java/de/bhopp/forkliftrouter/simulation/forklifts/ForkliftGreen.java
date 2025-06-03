package de.bhopp.forkliftrouter.simulation.forklifts;

import de.bhopp.forkliftrouter.simulation.VirtualForklift;
import java.awt.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ForkliftGreen extends VirtualForklift {
  public ForkliftGreen(ApplicationContext applicationContext) {
    super(new Color(6, 64, 43), applicationContext);
  }
}
