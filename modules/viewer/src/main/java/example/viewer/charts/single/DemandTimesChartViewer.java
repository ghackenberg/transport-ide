package example.viewer.charts;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import example.model.Demand;
import example.model.Model;
import example.model.Vehicle;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class DemandTimesChartViewer extends ChartViewer {

	public DemandTimesChartViewer(Model model, ExampleStatistics statistics, List<ExampleStatistics> baseline) {
		super(model, statistics, baseline, "Demand times", "Demands", "Time (in s)");
		
		renderer.setSeriesPaint(0, Color.GRAY);
		renderer.setSeriesPaint(1, Color.ORANGE);
		renderer.setSeriesPaint(2, Color.DARK_GRAY);
		renderer.setSeriesPaint(3, Color.RED);
		
		domain.setTickMarksVisible(false);
		domain.setTickLabelsVisible(false);
	}

	@Override
	public void update() {
		// Update values
		
		for (Demand demand : model.demands) {
			if (demand.pickup.time > model.time) {
				
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
				
				dataset.addValue(underdueWait / 1000, "Underdue (wait)", demand.toString());
				dataset.addValue(underdueRide / 1000, "Underdue (ride)", demand.toString());
				dataset.addValue(overdueWait / 1000, "Overdue (wait)", demand.toString());
				dataset.addValue(overdueRide / 1000, "Overdue (ride)", demand.toString());
				
				//System.out.println((underdueWait + underdueRide + overdueWait + overdueRide) / 1000);
				
			}
		}
		
		// Update range
		
		double max = 0;
		
		for (ExampleStatistics statistics : baseline) {
			for (Entry<Demand, Map<Double, Vehicle>> entry : statistics.demandDropoffTimes.entrySet()) {
				Demand demand = entry.getKey();
				Map<Double, Vehicle> times = entry.getValue();
				if (demand.pickup.time < model.time) {
					double dropoff = model.time;
					if (times.size() == 1) {
						dropoff = times.entrySet().iterator().next().getKey();
					}
					max = Math.max((dropoff - demand.pickup.time) / 1000, max);
				}
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
