package example.statistics;

import example.model.Demand;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;

public interface Statistics {

	public void recordCrossing(Vehicle vehicle, Segment previous, Segment next, double time);
	public void recordPickupDecline(Vehicle vehicle, Demand demand, double time);
	public void recordPickupAccept(Vehicle vehicle, Demand demand, double time);
	public void recordDropoff(Vehicle vehicle, Demand demand, double time);
	public void recordSpeed(Vehicle vehicle, double speed, double time);
	public void recordDistance(Vehicle vehicle, double distance, double time);
	public void recordChargeStart(Vehicle vehicle, Station station, double time);
	public void recordChargeEnd(Vehicle vehicle, Station station, double time);
	public void recordStep(double step, double time);
	public void reset();
	
}
