package example.controller.implementations;

import java.util.List;

import example.controller.Controller;
import example.model.Demand;
import example.model.Segment;
import example.model.Vehicle;

public class RandomController implements Controller {

	@Override
	public boolean selectAssignment(Vehicle vehicle, Demand demand) {
		return Math.random() > 0.5;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		return vehicle.initialSpeed; //vehicle.location.segment.speed; //return Math.random() * vehicle.location.segment.speed;
	}

	@Override
	public double selectSpeedUpdateTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public Segment selectSegment(Vehicle vehicle) {
		List<Segment> outgoing = vehicle.location.segment.end.outgoing;
		return outgoing.get((int) (Math.random() * outgoing.size()));
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public String toString() {
		return "Random controller";
	}

}