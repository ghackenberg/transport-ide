package example.controller.implementations;

import java.util.ArrayList;
import java.util.List;

import example.controller.Controller;
import example.model.Demand;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;

public class SwitchableController implements Controller {
	
	private Controller active;
	private List<Controller> controllers = new ArrayList<>();
	
	public void addController(Controller controller) {
		controllers.add(controller);
		if (active == null) {
			active = controller;
		}
	}
	
	public void removeController(Controller controller) {
		controllers.remove(controller);
		if (active == controller) {
			if (controllers.size() > 0) {
				active = controllers.get(0);
			} else {
				active = null;
			}
		}
	}
	
	public Controller[] getControllers() {
		return controllers.toArray(new Controller[] {});
	}
	
	public void setActiveController(Controller controller) {
		active = controller;
	}
	
	public Controller getActiveController() {
		return active;
	}

	@Override
	public boolean selectAssignment(Vehicle vehicle, Demand demand) {
		return active.selectAssignment(vehicle, demand);
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		return active.selectStation(vehicle, station);
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		return active.selectSpeed(vehicle);
	}

	@Override
	public double selectMaximumSpeedUpdateTimeout(Vehicle vehicle) {
		return active.selectMaximumSpeedUpdateTimeout(vehicle);
	}

	@Override
	public Segment selectSegment(Vehicle vehicle) {
		return active.selectSegment(vehicle);
	}

	@Override
	public void reset() {
		controllers.forEach(controller -> controller.reset());
	}

}
