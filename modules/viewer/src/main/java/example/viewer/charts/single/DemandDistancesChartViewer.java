package example.viewer.charts;

import java.awt.Color;
import java.util.List;

import example.model.Demand;
import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class DemandDistancesChartViewer extends ChartViewer {

	public DemandDistancesChartViewer(Model model, ExampleStatistics statistics, List<ExampleStatistics> baseline) {
		super(model, statistics, baseline, "Demand distances", "Demands", "Distance");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		// Update values
		
		for (Demand demand : model.demands) {
			double distance = statistics.demandDistances.get(demand);
			dataset.addValue(distance, "Distances", demand.toString());
		}
		
		// Update range
		
		double max = 0;
		
		for (ExampleStatistics statistics : baseline) {
			for (double distance : statistics.demandDistances.values()) {
				max = Math.max(distance, max);
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
