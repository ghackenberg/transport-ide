package example.model;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
	
	// Dynamische Eigenschaften (simuliert)
	public double load;
	public double speed;
	public int lane;
	public final Location location = new Location();
	public final List<Demand> demands = new ArrayList<>();
	public final List<Vehicle> collisions = new ArrayList<>();
	
	// Statische Eigenschaften (geparst)
	public String name;
	public double length;
	public double capacity;
	public double initialSpeed;
	public Location initialLocation;
	
	public void reset() {
		demands.clear();
		collisions.clear();
		load = 0;
		speed = initialSpeed;
		lane = -1;
		location.segment = initialLocation.segment;
		location.distance = initialLocation.distance;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}