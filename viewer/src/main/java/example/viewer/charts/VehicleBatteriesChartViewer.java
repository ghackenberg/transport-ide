package example.viewer.charts;

import java.awt.Color;

import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class VehicleBatteriesChartViewer extends ChartViewer {

	public VehicleBatteriesChartViewer(Model model, ExampleStatistics statistics) {
		super(model, statistics, "Vehicle batteries", "Vehicles", "Energy");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		model.vehicles.forEach(vehicle -> {
			double batteryLevel = vehicle.batteryLevel;
			double batteryCapacity = vehicle.batteryCapacity;
			dataset.addValue(batteryLevel, "Battery level", vehicle.name);
			dataset.addValue(batteryCapacity - batteryLevel, "Battery capacity", vehicle.name);
		});
	}

}
