package example.viewer.charts;

import java.awt.Color;

import example.model.Model;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class DemandTimesChartViewer extends ChartViewer {

	public DemandTimesChartViewer(Model model, ExampleStatistics statistics) {
		super(model, statistics, "Demand times", "Demands", "Time (in s)");
		
		renderer.setSeriesPaint(0, new Color(255, 255, 255, 0));
		renderer.setSeriesPaint(1, Color.GRAY);
		renderer.setSeriesPaint(2, Color.ORANGE);
		renderer.setSeriesPaint(3, Color.DARK_GRAY);
		renderer.setSeriesPaint(4, Color.RED);
	}

	@Override
	public void update() {
		model.demands.forEach(demand -> {
			if (demand.pickup.time > model.time) {
				dataset.addValue(0, "Offset", demand.toString());
				dataset.addValue(0, "Underdue (wait)", demand.toString());
				dataset.addValue(0, "Underdue (ride)", demand.toString());
				dataset.addValue(0, "Overdue (wait)", demand.toString());
				dataset.addValue(0, "Overdue (ride)", demand.toString());
			} else {
				double pickup = model.time;
				if (statistics.demandPickupAcceptTimes.get(demand).size() == 1) {
					pickup = statistics.demandPickupAcceptTimes.get(demand).entrySet().iterator().next().getKey();
				}
				double dropoff = model.time;
				if (statistics.demandDropoffTimes.get(demand).size() == 1) {
					dropoff = statistics.demandDropoffTimes.get(demand).entrySet().iterator().next().getKey();
				}
				double underdueWait = Math.min(demand.dropoff.time, pickup) - demand.pickup.time;
				double underdueRide = Math.min(demand.dropoff.time, dropoff) - Math.min(demand.dropoff.time, pickup);
				double overdueWait = Math.max(demand.dropoff.time, pickup) - demand.dropoff.time;
				double overdueRide = Math.max(demand.dropoff.time, dropoff) - Math.max(demand.dropoff.time, pickup);
				dataset.addValue(demand.pickup.time / 1000, "Offset", demand.toString());
				dataset.addValue(underdueWait / 1000, "Underdue (wait)", demand.toString());
				dataset.addValue(underdueRide / 1000, "Underdue (ride)", demand.toString());
				dataset.addValue(overdueWait / 1000, "Overdue (wait)", demand.toString());
				dataset.addValue(overdueRide / 1000, "Overdue (ride)", demand.toString());
			}
		});
	}

}
