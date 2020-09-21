package example.viewer.charts;

import java.awt.Color;

import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class SegmentTraversalsChartViewer extends ChartViewer {

	public SegmentTraversalsChartViewer(Model model, ExampleStatistics statistics) {
		super(model, statistics, "Segment traversals", "Segments", "Count");
		
		renderer.setSeriesPaint(0, Color.GREEN);
	}

	@Override
	public void update() {
		model.segments.forEach(segment -> {
			int traversals = statistics.segmentTraversals.get(segment);
			dataset.addValue(traversals, "Traversals", segment.toString());
		});
	}

}
