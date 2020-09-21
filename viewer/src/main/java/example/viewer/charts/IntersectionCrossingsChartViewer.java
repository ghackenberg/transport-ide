package example.viewer.charts;

import java.awt.Color;

import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class IntersectionCrossingsChartViewer extends ChartViewer {

	public IntersectionCrossingsChartViewer(Model model, ExampleStatistics statistics) {
		super(model, statistics, "Intersection crossings", "Intersections", "Count");
		
		renderer.setSeriesPaint(0, Color.GREEN);
	}

	@Override
	public void update() {
		model.intersections.forEach(intersection -> {
			int traversals = statistics.intersectionCrossings.get(intersection);
			dataset.addValue(traversals, "Crossings", intersection.name);
		});
	}

}
