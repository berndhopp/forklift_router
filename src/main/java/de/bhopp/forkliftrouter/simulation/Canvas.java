package de.bhopp.forkliftrouter.simulation;

import static java.lang.Math.round;
import static java.lang.String.format;

import de.bhopp.forkliftrouter.Location;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import org.springframework.stereotype.Component;

@Component
public class Canvas extends JPanel {
  private final List<VirtualForkliftController> forkliftControllers;
  private final List<Location> routePoints;

  public Canvas(List<VirtualForkliftController> forkliftsControllers, List<Location> routePoints) {
    this.forkliftControllers = forkliftsControllers;
    this.routePoints = routePoints;
  }

  @Override
  public void paint(Graphics g) {
    final var graphics = (Graphics2D) g;

    graphics.setRenderingHint(
        RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setRenderingHint(
        RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    graphics.setRenderingHint(
        RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    graphics.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

    graphics.clearRect(0, 0, this.getWidth(), this.getHeight());

    graphics.setColor(Color.BLACK);

    final var originalFont = graphics.getFont();

    graphics.setFont(new Font("Arial", Font.BOLD, 14));

    graphics.drawString("Forklift Router Simulation v1.0", 38, 35);

    graphics.setFont(originalFont);

    graphics.drawString("x          y          speed    avg speed", 40, 60);

    for (int i = 0; i < forkliftControllers.size(); i++) {
      final var controller = forkliftControllers.get(i);
      final var forklift = controller.getForklift();

      graphics.setColor(forklift.getColor());

      graphics.drawString(format("%03.0f", forklift.getLocation().x()), 40, 83 + i * 20);
      graphics.drawString(format("%03.0f", forklift.getLocation().y()), 80, 83 + i * 20);
      graphics.drawString(format("%02.1f", forklift.getCurrentSpeed()), 121, 83 + i * 20);
      graphics.drawString(
          format("%02.1f", controller.getAverageSpeedOutsideStations()), 167, 83 + i * 20);
      graphics.drawOval(
          (int) round(forklift.getLocation().y()) - 10,
          (int) round(forklift.getLocation().x()) - 10,
          20,
          20);

      if (forklift.loadOrUnloadProgress() != -1) {
        graphics.fillArc(
            (int) round(forklift.getLocation().y()) - 10,
            (int) round(forklift.getLocation().x()) - 10,
            20,
            20,
            90,
            -round(forklift.loadOrUnloadProgress() * 360));
      }
    }

    graphics.setColor(Color.BLACK);

    for (int i = 0; i < routePoints.size(); i++) {
      final var routePoint = routePoints.get(i);

      graphics.drawOval((int) round(routePoint.y()) - 15, (int) round(routePoint.x()) - 15, 30, 30);

      graphics.drawString("" + (i + 1), (int) routePoint.y() - 3, (int) routePoint.x() + 4);
    }
  }
}
