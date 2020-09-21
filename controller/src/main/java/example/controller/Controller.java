package example.controller;

import example.model.Demand;
import example.model.Segment;
import example.model.Vehicle;

public interface Controller {

	/**
	 * Decide whether to pickup the demand or not.
	 * 
	 * @param vehicle The vehicle for potential pickup.
	 * @param demand The demand to pickup.
	 * 
	 * @return True if pickup, false otherwise.
	 */
	public boolean selectAssignment(Vehicle vehicle, Demand demand);
	
	/**
	 * Select the upcoming vehicle speed.
	 * 
	 * @param vehicle The vehicle for speed update.
	 * 
	 * @return The new speed.
	 */
	public double selectSpeed(Vehicle vehicle);
	
	/**
	 * Select the timeout for new speed decision.
	 * 
	 * @param vehicle The vehicle for which the timeout holds.
	 * 
	 * @return The timeout in milliseconds of simulation time.
	 */
	public double selectSpeedUpdateTimeout(Vehicle vehicle);
	
	/**
	 * Select the next segment after reaching the end of the previous segment.
	 * 
	 * @param vehicle The vehicle for which the routing decision should be taken.
	 * 
	 * @return The selected segment.
	 */
	public Segment selectSegment(Vehicle vehicle);
	
	public void reset();
	
}
