package example.simulator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import example.controller.Controller;
import example.model.Demand;
import example.model.Model;
import example.model.Segment;
import example.model.Vehicle;
import example.simulator.exceptions.CollisionException;
import example.simulator.exceptions.InvalidException;
import example.simulator.exceptions.InvalidRouteException;
import example.simulator.exceptions.InvalidSpeedException;
import example.simulator.exceptions.InvalidTimeoutException;
import example.statistics.Statistics;

public class Simulator<S extends Statistics> {
	
	private boolean stop;
	private boolean pause;
	private boolean step;
	
	private Model model;
	private Controller controller;
	private S statistics;
	private double maxModelTimeStep;
	private double ratioModelRealTime;
	
	private File runsFolder;
	private File runFolder;
	
	private Synchronizer synchronizer;
	
	private Map<Demand, Map<Double, Vehicle>> declines = new HashMap<>();
	
	public interface Handler {
		void handle(InvalidException exception);
	}
	
	private Runnable handleUpdated;
	private Runnable handleFinished;
	private Runnable handleStopped;
	private Handler handleException;
	
	private Thread thread;
	
	public Simulator(Model model, Controller controller, S statistics, double maxModelTimeStep, double ratioModelRealTime, File runsFolder) {
		this(model, controller, statistics, maxModelTimeStep, ratioModelRealTime, runsFolder, new Synchronizer(1));
	}
	
	public Simulator(Model model, Controller controller, S statistics, double maxModelTimeStep, double ratioModelRealTime, File runsFolder, Synchronizer synchronizer) {
		this.model = model;
		this.controller = controller;
		this.statistics = statistics;
		this.maxModelTimeStep = maxModelTimeStep;
		this.ratioModelRealTime = ratioModelRealTime;
		this.runsFolder = runsFolder;
		this.synchronizer = synchronizer;
	}
	
	public Model getModel() {
		return model;
	}
	
	public S getStatistics() {
		return statistics;
	}
	
	public double getMaxModelTimeStep() {
		return maxModelTimeStep;
	}
	
	public double getRatioModelRealTime() {
		return ratioModelRealTime;
	}
	
	public synchronized void setMaxModelTimeStep(double value) {
		maxModelTimeStep = value;
	}
	
	public synchronized void setRatioModelRealTime(double value) {
		ratioModelRealTime = value;
	}
	
	public void setHandleUpdated(Runnable value) {
		handleUpdated = value;
	}
	
	public void setHandleFinished(Runnable value) {
		handleFinished = value;
	}
	
	public void setHandleStopped(Runnable value) {
		handleStopped = value;
	}
	
	public void setHandleException(Handler value) {
		handleException = value;
	}
	
	public File getRunsFolder() {
		return runsFolder;
	}
	
	public File getRunFolder() {
		return runFolder;
	}
	
	public void start() {
		System.out.println("Simulator.start");
		
		model.reset();
		controller.reset();
		statistics.reset();
		
		declines.clear();
		model.demands.forEach(demand -> {
			declines.put(demand, new HashMap<>());
		});
		
		stop = false;
		
		Date date = Calendar.getInstance().getTime();
		String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
		
		runFolder = new File(runsFolder, name);
		runFolder.mkdir();
		
		thread = new Thread(this::loop);
		thread.start();
	}
	
	public void stop() {
		try {
			System.out.println("Simulator.stop");
			
			stop = true;
			
			synchronized (this) {
				notify();
			}
			
			thread.join();
			thread = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		System.out.println("Simulator.pause");
		
		pause = true;
	}
	
	public void resume() {
		System.out.println("Simulator.resume");
		
		pause = false;
		
		synchronized (this) {
			notify();
		}
	}
	
	public void step() {
		System.out.println("Simulator.step");
		
		step = true;
		
		synchronized (this) {
			notify();
		}
	}
	
	private void loop() {
		synchronizer.start();
		
		try {
			updateCollisions();
			if (handleUpdated != null) {
				handleUpdated.run();
			}
			while (!stop && !model.isFinished()) {
				update();
				if (handleUpdated != null) {
					handleUpdated.run();
				}
			}
			if (model.isFinished()) {
				if (handleFinished != null) {
					handleFinished.run();
				}
			} else {
				if (handleStopped != null) {
					handleStopped.run();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvalidException e) {
			if (handleException != null) {
				handleException.handle(e);
			}
		}
		
		synchronizer.finish();
	}
	
	private synchronized void update() throws InterruptedException, InvalidException {
		
		//System.out.println("Simulator.step");
		
		while (!stop && pause && !step) {
			wait();
		}
		
		step = false;
		
		if (stop) {
			return;
		}
		
		// Remember real time before calculation
		final double realTimeBefore = System.currentTimeMillis();
		// Remember model time before calculation
		final double modelTimeBefore = model.time;
		
		// Perform calculation (and advance simulation time)
		calculate();
		
		// Remember real time after calculation
		final double realTimeAfter = System.currentTimeMillis();
		// Remember model time after calculation
		final double modelTimeAfter = model.time;
		
		// Calculate actual time advance
		final double realTimeDelta = realTimeAfter - realTimeBefore;
		// Calculate simulation time advance
		final double modelTimeDelta = modelTimeAfter - modelTimeBefore;
		
		// Calculate difference between simulation time advance and actual time advance 
		final double difference = modelTimeDelta - realTimeDelta;
		
		// Sleep for the difference in time advances
		if (!pause) {
			Thread.sleep(Math.max((long) (difference / ratioModelRealTime), 0));
		}
		
	}
	
	private void calculate() throws InvalidException, InterruptedException {
		
		//System.out.println("Simulator.calculate");
		
		// Initialize model time step
		double modelTimeStep = maxModelTimeStep;
		
		// Update vehicle segment
		for (Vehicle vehicle : model.vehicles) {
			// Check location distance
			if (vehicle.location.distance == vehicle.location.segment.getLength()) {
				// Remember segment
				Segment previous = vehicle.location.segment;
				// Select segment
				Segment next = controller.selectSegment(vehicle);
				// Check segment
				if (!previous.end.outgoing.contains(next)) {
					throw new InvalidRouteException(vehicle, previous, next);
				}
				// Update segment
				vehicle.location.segment = next;
				// Reset segment distance
				vehicle.location.distance = 0;
				// Update speed
				vehicle.speed = controller.selectSpeed(vehicle);
				// Update statistics
				statistics.recordCrossing(vehicle, previous, next, model.time);
				statistics.recordSpeed(vehicle, vehicle.speed, model.time);
				// Update model time step
				modelTimeStep = 0;
			}
		}
		
		// Pickup demands
		for (Demand demand : model.demands) {
			// Check demand relevance
			if (demand.done == false && demand.vehicle == null && demand.pickup.time <= model.time) {
				// Process vehicles
				for (Vehicle vehicle : model.vehicles) {
					// Compare segment
					if (demand.pickup.location.segment == vehicle.location.segment) {
						// Compare distance on segment
						if (demand.pickup.location.distance == vehicle.location.distance) {
							// Compare load vs. capacity
							if (demand.size + vehicle.load <= vehicle.capacity) {
								// Declined before?
								if (!declines.get(demand).containsKey(model.time) || declines.get(demand).get(model.time) != vehicle) {
									// Ask controller for pickup decision
									if (controller.selectAssignment(vehicle, demand)) {
										// Update demand
										demand.vehicle = vehicle;
										// Update vehicle
										vehicle.load += demand.size;
										vehicle.demands.add(demand);
										// Update statistics
										statistics.recordPickupAccept(vehicle, demand, model.time);
										// Update model time step
										modelTimeStep = 0;
									} else {
										// Update declines
										declines.get(demand).put(model.time, vehicle);
										// Update statistics
										statistics.recordPickupDecline(vehicle, demand, model.time);
										// Update model time step
										modelTimeStep = 0;
									}
								}
							}
						}
					}
				}
			}
		}
		
		// Dropoff demands
		for (Vehicle vehicle : model.vehicles) {
			// Process demands
			for (int index = 0; index < vehicle.demands.size(); index++) {
				// Obtain demand
				Demand demand = vehicle.demands.get(index);
				// Compare segment
				if (demand.dropoff.location.segment == vehicle.location.segment) {
					// Compare distance on segment
					if (demand.dropoff.location.distance == vehicle.location.distance) {
						// Update demand
						demand.done = true;
						demand.vehicle = null;
						// Update vehicle
						vehicle.load -= demand.size;
						vehicle.demands.remove(index--);
						// Update statistics
						statistics.recordDropoff(vehicle, demand, model.time);
						// Update model time step
						modelTimeStep = 0;
					}
				}
			}
		}
		
		// Update vehicle speed
		for (Vehicle vehicle : model.vehicles) {
			// Select speed
			double speed = controller.selectSpeed(vehicle);
			// Check speed
			if (speed > vehicle.location.segment.speed) {
				throw new InvalidSpeedException(vehicle, speed);
			}
			// Update speed
			vehicle.speed = speed;
			// Update statistics
			statistics.recordSpeed(vehicle, speed, model.time);
		}
		
		// Duration until speed selection
		for (Vehicle vehicle : model.vehicles) {
			// Select timeout
			double timeout = controller.selectSpeedUpdateTimeout(vehicle);
			// Check timeout
			if (timeout < 0) {
				throw new InvalidTimeoutException(vehicle, timeout);
			}
			// Calculate duration
			modelTimeStep = Math.min(modelTimeStep, timeout);
		}
		
		// Duration until segment end
		for (Vehicle vehicle : model.vehicles) {
			// Speed in meter per millisecond
			double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
			// Delta in meter
			double delta = vehicle.location.segment.getLength() - vehicle.location.distance;
			// Duration in milliseconds
			double duration = delta / speed;
			// Update model time step
			modelTimeStep = Math.min(modelTimeStep, duration);
		}
		
		// Duration until demand appearance
		for (Demand demand : model.demands) {
			if (demand.pickup.time > model.time) {
				modelTimeStep = Math.min(modelTimeStep, demand.pickup.time - model.time);
			}
		}
		
		// Duration until demand overdue
		for (Demand demand : model.demands) {
			if (demand.done == false && demand.dropoff.time > model.time) {
				modelTimeStep = Math.min(modelTimeStep, demand.dropoff.time - model.time);
			}
		}
		
		// Duration until demand pickup
		for (Demand demand : model.demands) {
			// Check demand relevance
			if (demand.done == false && demand.vehicle == null && demand.pickup.time <= model.time) {
				// Process vehicles
				for (Vehicle vehicle : model.vehicles) {
					// Pickup on same segment
					if (demand.pickup.location.segment == vehicle.location.segment) {
						// Pickup ahead
						if (demand.pickup.location.distance > vehicle.location.distance) {
							// Enough capactiy?
							if (demand.size <= vehicle.capacity - vehicle.load) {
								// Speed in meter per millisecond
								double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
								// Delta in meter
								double delta = demand.pickup.location.distance - vehicle.location.distance;
								// Duration in milliseconds
								double duration = delta / speed;
								// Update model time step;
								modelTimeStep = Math.min(modelTimeStep, duration);
							}
						}
					}
				}
			}
		}
		
		// Duration until demand dropoff
		for (Vehicle vehicle : model.vehicles) {
			// Process demands
			for (Demand demand : vehicle.demands) {
				// Dropoff on same segment
				if (vehicle.location.segment == demand.dropoff.location.segment) {
					// Dropoff ahead
					if (vehicle.location.distance < demand.dropoff.location.distance) {
						// Speed in meter per millisecond
						double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
						// Delta in meter
						double delta = demand.dropoff.location.distance - vehicle.location.distance;
						// Duration in milliseconds
						double duration = delta / speed;
						// Update model time step;
						modelTimeStep = Math.min(modelTimeStep, duration);
					}
				}
			}
		}
		
		// Duration until vehicle attach/detach
		for (int i = 0; i < model.vehicles.size(); i++) {
			Vehicle outer = model.vehicles.get(i);
			
			double outerBack = outer.location.distance - outer.length / 2;
			double outerFront = outer.location.distance + outer.length / 2;
			
			for (int j = i + 1; j < model.vehicles.size(); j++) {
				Vehicle inner = model.vehicles.get(j);
				
				double innerBack = inner.location.distance - inner.length / 2;
				double innerFront = inner.location.distance + inner.length / 2;
				
				// On same segment?
				if (outer.location.segment == inner.location.segment) {	
					if (inner.speed < outer.speed) {
						double speed = (outer.speed - inner.speed) * 1000.0 / 60.0 / 60.0 / 1000.0;
						
						// Attach
						if (outerFront < innerBack) {
							double distance = innerBack - outerFront;
							double duration = distance / speed;
							modelTimeStep = Math.min(modelTimeStep, duration);
						}
						// Detach
						if (outerBack < innerFront) {
							double distance = innerFront - outerBack;
							double duration = distance / speed;
							modelTimeStep = Math.min(modelTimeStep, duration);
						}
					}
					if (outer.speed < inner.speed) {
						double speed = (inner.speed - outer.speed) * 1000.0 / 60.0 / 60.0 / 1000.0;
						
						// Attach
						if (innerFront < outerBack) {
							double distance = outerBack - innerFront;
							double duration = distance / speed;
							modelTimeStep = Math.min(modelTimeStep, duration);
						}
						// Detach
						if (innerBack < outerFront) {
							double distance = outerFront - innerBack;
							double duration = distance / speed;
							modelTimeStep = Math.min(modelTimeStep, duration);
						}
					}
				}
			}
		}
		
		// Synchronize simulators
		modelTimeStep = synchronizer.vote(modelTimeStep);
		
		// Update statistics
		statistics.recordStep(modelTimeStep, model.time);
		
		// Update vehicle position
		for (Vehicle vehicle : model.vehicles) {
			// Speed in meter per millisecond
			double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
			// Delta in meter
			double delta = speed * modelTimeStep;
			// Update distance
			vehicle.location.distance += delta;
			// Update statistics
			statistics.recordDistance(vehicle, delta, model.time);
		}
		
		// Update model time
		model.time += modelTimeStep;
		
		// Update collisions
		updateCollisions();
		
	}
	
	private void updateCollisions() throws CollisionException {
		Map<Segment, List<Vehicle>> map = new HashMap<>();
		// Initialize map
		model.segments.forEach(segment -> {
			map.put(segment, new ArrayList<>());
		});
		model.vehicles.forEach(vehicle -> {
			map.get(vehicle.location.segment).add(vehicle);
		});
		model.segments.forEach(segment -> {
			map.get(segment).sort((first, second) -> {
				if (first.speed != second.speed) {
					return (int) Math.signum(first.speed - second.speed);
				} else {
					if (first.location.distance != second.location.distance) {
						return (int) Math.signum(first.location.distance - second.location.distance);
					} else {
						return first.name.compareTo(second.name);
					}
				}
			});
		});
		// Clear vehicles
		model.vehicles.forEach(vehicle -> {
			// Clear collisions
			vehicle.collisions.clear();
			vehicle.collisions.add(vehicle);
			// Clear lane
			vehicle.lane = -1;
		});
		// Clear segments
		model.segments.forEach(segment -> {
			// Clear load
			segment.load = 0;
			// Clear collisions
			segment.collisions = null;
		});
		// Collect collisions
		for (int i = 0; i < model.vehicles.size(); i++) {
			Vehicle outer = model.vehicles.get(i);
			// Calculate front/back
			double outerBack = outer.location.distance - outer.length / 2;
			double outerFront = outer.location.distance + outer.length / 2;
			// Compare each other vehicle
			for (int j = i + 1; j < model.vehicles.size(); j++) {
				Vehicle inner = model.vehicles.get(j);
				// Calculate front/back
				double innerBack = inner.location.distance - inner.length / 2;
				double innerFront = inner.location.distance + inner.length / 2;
				// On same segment?
				if (outer.location.segment == inner.location.segment) {
					// Outer faster
					if (inner.speed < outer.speed) {
						if (outerBack < innerFront && outerFront >= innerBack) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
					}
					// Inner faster
					if (outer.speed < inner.speed) {
						if (innerBack < outerFront && innerFront >= outerBack) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
					}
					// Same speed
					if (outer.speed == inner.speed) {
						if (outerBack < innerFront && outerFront > innerBack) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
						if (innerBack < outerFront && innerFront > outerBack) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
					}
				}
			}
		}
		// Update vehicles
		map.entrySet().forEach(entry -> {
			entry.getValue().forEach(outer-> {
				for (int lane = 0; lane < entry.getValue().size(); lane++) {
					boolean free = true;
					for (Vehicle inner : outer.collisions) {
						if (inner.lane == lane) {
							free = false;
							break;
						}
					}
					if (free) {
						outer.lane = lane;
						break;
					}
				}
			});
		});
		// Update segments
		model.vehicles.forEach(vehicle -> {
			if (vehicle.location.segment.load < vehicle.lane + 1) {
				vehicle.location.segment.load = vehicle.lane + 1;
				vehicle.location.segment.collisions = vehicle.collisions;
			}
		});
		// Throw exceptions
		for (Segment segment : model.segments) {
			if (segment.load > segment.lanes) {
				throw new CollisionException(segment, segment.collisions);
			}
		}
	}

}