package example.model;

import java.util.List;

public class Segment {

	// Dynamische Eigenschaften (simuliert)
	public double load;
	public List<Vehicle> collisions;
	
	// Statische Eigenschaften (geparst)
	public Intersection start;
	public Intersection end;
	public double lanes;
	public double speed;
	
	public double getLength() {
		double dx = start.coordinate.latitude - end.coordinate.latitude;
		double dy = start.coordinate.longitude - end.coordinate.longitude;
		double dz = start.coordinate.elevation - end.coordinate.elevation;
		
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	@Override
	public String toString() {
		return start.name + "->" + end.name;
	}

}
