package example.viewer.charts;

import java.awt.Color;

import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class VehicleDistancesChartViewer extends ChartViewer {

	public VehicleDistancesChartViewer(Model model, ExampleStatistics statistics) {
		super(model, statistics, "Vehicle distances", "Vehicles", "Distance");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		model.vehicles.forEach(vehicle -> {
			double distance = statistics.vehicleDistances.get(vehicle);
			dataset.addValue(distance, "Distances", vehicle.name);
		});
	}

}
