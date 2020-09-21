package example.model;

public class Demand {

	// Dynamische Eigenschaften (simuliert)
	public Vehicle vehicle;
	public boolean done;
	
	// Statische Eigenschaften (geparst)
	public LocationTime pickup;
	public LocationTime dropoff;
	public double size;
	
	public Demand() {
		
	}
	
	public Demand(LocationTime pickup, LocationTime dropoff, double size) {
		this.pickup = pickup;
		this.dropoff = dropoff;
		this.size = size;
	}
	
	public Demand(Segment pickupSegment, double pickupDistance, double pickupTime, Segment dropoffSegment, double dropoffDistance, double dropoffTime, double size) {
		this.pickup = new LocationTime(pickupSegment, pickupDistance, pickupTime);
		this.dropoff = new LocationTime(dropoffSegment, dropoffDistance, dropoffTime);
		this.size = size;
	}
	
	public void reset() {
		vehicle = null;
		done = false;
	}
	
	@Override
	public String toString( ) {
		return pickup.toString() + " " + dropoff.toString();
	}
	
}
