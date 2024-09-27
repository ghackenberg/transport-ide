package example.viewer.charts;

import java.awt.Color;
import java.util.List;

import example.model.Intersection;
import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class IntersectionCrossingsChartViewer extends ChartViewer {

	public IntersectionCrossingsChartViewer(Model model, ExampleStatistics statistics, List<ExampleStatistics> baseline) {
		super(model, statistics, baseline, "Intersection crossings", "Intersections", "Count");
		
		renderer.setSeriesPaint(0, Color.GREEN);
	}

	@Override
	public void update() {
		// Update values
		
		for (Intersection intersection : model.intersections) {
			int traversals = statistics.intersectionCrossings.get(intersection);
			dataset.addValue(traversals, "Crossings", intersection.name);
		}
		
		// Update range
		
		double max = 1;
		
		for (ExampleStatistics statistics : baseline) {
			for (int traversals : statistics.intersectionCrossings.values()) {
				max = Math.max(traversals, max);
			}
		}
		
		range.setRange(0, max);
	}

}
