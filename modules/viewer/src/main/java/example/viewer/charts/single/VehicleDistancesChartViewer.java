package example.viewer.charts;

import java.awt.Color;
import java.util.List;

import example.model.Model;
import example.model.Vehicle;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class VehicleDistancesChartViewer extends ChartViewer {

	public VehicleDistancesChartViewer(Model model, ExampleStatistics statistics, List<ExampleStatistics> baseline) {
		super(model, statistics, baseline, "Vehicle distances", "Vehicles", "Distance");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		// Update values
		
		for (Vehicle vehicle : model.vehicles) {
			double distance = statistics.vehicleDistances.get(vehicle);
			dataset.addValue(distance, "Distances", vehicle.name);
		}
		
		// Update range
		
		double max = 0;
		
		for (ExampleStatistics statistics : baseline) {
			for (double distance : statistics.vehicleDistances.values()) {
				max = Math.max(distance, max);
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
