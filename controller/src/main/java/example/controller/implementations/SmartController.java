package example.controller.implementations;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import example.controller.Controller;
import example.controller.adapters.GraphAdapter;
import example.model.Demand;
import example.model.Intersection;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;

public class SmartController implements Controller {
	
	private final Model model;
	private final Graph<Intersection, Segment> graph; 
	private final ShortestPathAlgorithm<Intersection, Segment> algorithm;
	
	public SmartController(Model model) {
		this.model = model;
		graph = new GraphAdapter(model);
		algorithm = new DijkstraShortestPath<>(graph);
	}

	@Override
	public boolean selectAssignment(Vehicle vehicle, Demand demand) {
		return true;
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		Segment previous = vehicle.location.segment;
		
		SingleSourcePaths<Intersection, Segment> paths = algorithm.getPaths(previous.end);
		
		double minimumWeight = Double.MAX_VALUE;
		
		for (Station otherStation : model.stations) {
			if (vehicle.location.segment == otherStation.location.segment && vehicle.location.distance < otherStation.location.distance) {
				double distance = otherStation.location.distance - vehicle.location.distance;
				if (minimumWeight > distance) {
					minimumWeight = distance;
				}
			} else if (previous.end == otherStation.location.segment.start) {
				double distance = previous.getLength() - vehicle.location.distance + otherStation.location.distance;
				if (minimumWeight > distance) {
					minimumWeight = distance;
				}
			} else {
				GraphPath<Intersection, Segment> path = paths.getPath(otherStation.location.segment.start);
				if (path.getLength() > 0) {
					double distance = previous.getLength() - vehicle.location.distance + path.getWeight() + otherStation.location.distance;
					if (minimumWeight > distance) {
						minimumWeight = distance;
					}
				}
			}
		}
		
		return vehicle.batteryLevel < minimumWeight;
	}
	
	@Override
	public boolean unselectStation(Vehicle vehicle) {
		return false;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		Segment previous = vehicle.location.segment;
		
		SingleSourcePaths<Intersection, Segment> paths = algorithm.getPaths(previous.end);
		
		double minimumWeight = Double.MAX_VALUE;
		
		for (Station station : model.stations) {
			if (vehicle.location.segment == station.location.segment && vehicle.location.distance < station.location.distance) {
				double distance = station.location.distance - vehicle.location.distance;
				if (minimumWeight > distance) {
					minimumWeight = distance;
				}
			} else if (previous.end == station.location.segment.start) {
				double distance = previous.getLength() - vehicle.location.distance + station.location.distance;
				if (minimumWeight > distance) {
					minimumWeight = distance;
				}
			} else {
				GraphPath<Intersection, Segment> path = paths.getPath(station.location.segment.start);
				if (path.getLength() > 0) {
					double distance = previous.getLength() - vehicle.location.distance + path.getWeight() + station.location.distance;
					if (minimumWeight > distance) {
						minimumWeight = distance;
					}
				}
			}
		}
		
		for (Station station : model.stations) {
			if (vehicle.location.segment == station.location.segment) {
				if (vehicle.location.distance == station.location.distance) {
					if (vehicle.batteryLevel < minimumWeight) {
						return 0;
					}
				}
			}
		}
		
		return vehicle.location.segment.speed;
	}

	@Override
	public double selectMaximumSpeedSelectionTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public double selectMaximumStationSelectionTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public Segment selectSegment(Vehicle vehicle) {
		Segment previous = vehicle.location.segment;
		
		SingleSourcePaths<Intersection, Segment> paths = algorithm.getPaths(previous.end);
		
		double minimumWeight = Double.MAX_VALUE;
		Segment minimumEdge = previous.end.outgoing.get((int) (Math.random() * previous.end.outgoing.size()));
		
		for (Station station : model.stations) {
			if (previous.end == station.location.segment.start) {
				double distance = station.location.distance;
				if (minimumWeight > distance) {
					minimumWeight = distance;
					minimumEdge = station.location.segment;
				}
			} else {
				GraphPath<Intersection, Segment> path = paths.getPath(station.location.segment.start);
				if (path.getLength() > 0) {
					double distance = path.getWeight() + station.location.distance;
					if (minimumWeight > distance) {
						minimumWeight = distance;
						minimumEdge = path.getEdgeList().get(0);
					}
				}
			}
		}
		
		if (vehicle.batteryLevel < minimumWeight * 1.5) {
			return minimumEdge;
		}
		
		minimumWeight = Double.MAX_VALUE;
		
		for (Demand demand : vehicle.demands) {
			if (previous.end == demand.dropoff.location.segment.start) {
				double distance = demand.dropoff.location.distance;
				if (minimumWeight > distance) {
					minimumWeight = distance;
					minimumEdge = demand.dropoff.location.segment;
				}
			} else {
				GraphPath<Intersection, Segment> path = paths.getPath(demand.dropoff.location.segment.start);
				if (path.getLength() > 0) {
					double distance = path.getWeight() + demand.dropoff.location.distance;
					if (minimumWeight > distance) {
						minimumWeight = distance;
						minimumEdge = path.getEdgeList().get(0);
					}
				}
			}
		}
		
		if (vehicle.demands.size() == 0) {
			for (Demand demand : model.demands) {
				if (demand.done == false && demand.vehicle == null && demand.pickup.time <= model.time) {
					if (vehicle.loadLevel + demand.size <= vehicle.loadCapacity) {
						if (vehicle.location.segment.end == demand.pickup.location.segment.start) {
							double distance = demand.pickup.location.distance;
							if (minimumWeight > distance) {
								minimumWeight = distance;
								minimumEdge = demand.pickup.location.segment;
							}
						} else {
							GraphPath<Intersection, Segment> path = paths.getPath(demand.pickup.location.segment.start);
							if (path.getLength() > 0) {
								double distance = path.getWeight() + demand.pickup.location.distance;
								if (minimumWeight > distance) {
									minimumWeight = distance;
									minimumEdge = path.getEdgeList().get(0);
								}
							}
						}
					}
				}
			}
		}
		
		return minimumEdge;
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public String toString() {
		return "Smart controller";
	}

}
