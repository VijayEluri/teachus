package dk.frankbille.teachus.frontend.components.jfreechart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

import dk.frankbille.teachus.frontend.TeachUsSession;

import wicket.markup.html.image.resource.RenderedDynamicImageResource;

public abstract class JFreeChartResource extends RenderedDynamicImageResource {
	private static final long serialVersionUID = 1L;

	public JFreeChartResource(int width, int height) {
		super(width, height);
	}

	@Override
	protected boolean render(Graphics2D graphics) {		
		Plot plot = getPlot();
		plot.setForegroundAlpha(0.5f);
		plot.setBackgroundPaint(JFreeChart.DEFAULT_BACKGROUND_PAINT);
		plot.setNoDataMessage(TeachUsSession.get().getString("Stats.noData"));
		
		JFreeChart chart = new JFreeChart(null, null, plot, getCreateLegend());
		chart.setBackgroundPaint(Color.WHITE);
		
		chart.draw(graphics, new Rectangle2D.Double(0, 0, getWidth(), getHeight()));		
		
		return true;
	}

	protected abstract Plot getPlot();
	
	protected boolean getCreateLegend() {
		return false;
	}
	
}
