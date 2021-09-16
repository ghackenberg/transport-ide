package example.model;

public class Station {
	
	// Dynamische Eigenschaften (simuliert)
	public Vehicle vehicle;

	// Statische Eigenschaften (geparst)
	public Location location;
	
	@Override
	public String toString() {
		return location.toString();
	}

}
