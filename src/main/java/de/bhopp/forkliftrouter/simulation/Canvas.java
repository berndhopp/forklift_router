package de.bhopp.forkliftrouter.simulation;

import de.bhopp.forkliftrouter.domain.Location;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import org.springframework.stereotype.Component;

@Component
public class Canvas extends JPanel {
  private final List<SimulatedForklift> forklifts;
  private final List<Location> routePoints;

  public Canvas(List<SimulatedForklift> forklifts, List<Location> routePoints) {
    this.forklifts = forklifts;
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

    forklifts.forEach(
        v -> {
          graphics.setColor(v.getColor());

          graphics.drawOval(
              (int) Math.round(v.getLocation().y()) - 10,
              (int) Math.round(v.getLocation().x()) - 10,
              20,
              20);

          if (v.loadOrUnloadProgress() != -1) {
            graphics.fillArc(
                (int) Math.round(v.getLocation().y()) - 10,
                (int) Math.round(v.getLocation().x()) - 10,
                20,
                20,
                90,
                -Math.round(v.loadOrUnloadProgress() * 360));
          }
        });

    graphics.setColor(Color.BLACK);

    for (int i = 0; i < routePoints.size(); i++) {
      Location v = routePoints.get(i);

      graphics.drawOval((int) Math.round(v.y()) - 15, (int) Math.round(v.x()) - 15, 30, 30);

      graphics.drawString("" + (i + 1), (int) v.y() - 3, (int) v.x() + 4);
    }
  }
}
