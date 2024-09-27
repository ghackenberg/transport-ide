package example.viewer.charts;

import java.awt.Color;
import java.util.List;

import example.model.Model;
import example.model.Segment;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class SegmentTraversalsChartViewer extends ChartViewer {

	public SegmentTraversalsChartViewer(Model model, ExampleStatistics statistics, List<ExampleStatistics> baseline) {
		super(model, statistics, baseline, "Segment traversals", "Segments", "Count");
		
		renderer.setSeriesPaint(0, Color.GREEN);
	}

	@Override
	public void update() {
		// Update values
		
		for (Segment segment : model.segments) {
			int traversals = statistics.segmentTraversals.get(segment);
			dataset.addValue(traversals, "Traversals", segment.toString());
		}
		
		// Update range
		
		double max = 0;
		
		for (ExampleStatistics statistics : baseline) {
			for (int traversals : statistics.segmentTraversals.values()) {
				max = Math.max(traversals, max);
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
