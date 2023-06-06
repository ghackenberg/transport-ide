package example.viewer.charts;

import java.awt.Color;

import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class DemandDistancesChartViewer extends ChartViewer {

	public DemandDistancesChartViewer(Model model, ExampleStatistics statistics) {
		super(model, statistics, "Demand distances", "Demands", "Distance");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		model.demands.forEach(demand -> {
			double distance = statistics.demandDistances.get(demand);
			dataset.addValue(distance, "Distances", demand.toString());
		});
	}

}
