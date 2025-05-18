package de.bhopp.forkliftrouter.simulation;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Simulation extends JFrame {
  Simulation(Canvas canvas, PhysicsEngine physicsEngine) {
    add(canvas);
    setSize(1200, 800);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                physicsEngine.update();
                canvas.removeAll();
                canvas.repaint();
              }
            },
            0,
            50);
  }

  public static void main(String[] args) {
    var ctx =
        new SpringApplicationBuilder(Simulation.class)
            .headless(false)
            .web(WebApplicationType.NONE)
            .run(args);

    EventQueue.invokeLater(
        () -> {
          var ex = ctx.getBean(Simulation.class);
          ex.setVisible(true);
        });
  }
}
