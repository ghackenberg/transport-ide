package example.model;

public class Location {

	// Statische Eigenschaften (geparst) oder dynamische Eigenschaften (simuliert)
	public Segment segment;
	public double distance;
	
	public Location() {
		
	}
	
	public Location(Segment segment, double distance) {
		this.segment = segment;
		this.distance = distance;
	}
	
	public Coordinate toCoordinate() {
		
		Coordinate start = segment.start.coordinate;
		Coordinate end = segment.end.coordinate;
		
		Coordinate coordinate = new Coordinate();
		
		coordinate.latitude = start.latitude + (end.latitude - start.latitude) * distance / segment.getLength();
		coordinate.longitude = start.longitude + (end.longitude - start.longitude) * distance / segment.getLength();
		coordinate.elevation = start.elevation + (end.elevation - start.elevation) * distance / segment.getLength();
		
		return coordinate;
		
	}
	
	@Override
	public String toString() {
		return segment + ":" + Math.round(distance / segment.getLength() * 100);
	}
	
}
