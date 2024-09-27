package example.viewer.charts;

import java.util.List;

import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public abstract class MultipleChartViewer extends ChartViewer {
	
	protected List<Simulator<ExampleStatistics>> simulators;

	public MultipleChartViewer(List<Simulator<ExampleStatistics>> simulators, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.simulators = simulators;
	}

}
