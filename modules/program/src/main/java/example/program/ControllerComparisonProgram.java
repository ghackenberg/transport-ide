package example.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import example.controller.Controller;
import example.controller.implementations.GreedyController;
import example.controller.implementations.RandomController;
import example.controller.implementations.SmartController;
import example.model.Demand;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.parser.Parser;
import example.parser.exceptions.DirectoryException;
import example.parser.exceptions.MissingException;
import example.program.dialogs.ModelOpenDialog;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.simulator.Synchronizer;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.MultipleViewer;

public class ControllerComparisonProgram {

	public static void main(String[] args) {
		try {
			// Choose folder
		
			File modelFolder = ModelOpenDialog.choose();
			
			if (modelFolder == null) {
				return;
			}
			
			// Select runs folder
			
			File runsFolder = new File(modelFolder, "runs");
			
			if (!runsFolder.exists())
				runsFolder.mkdir();
			else if (!runsFolder.isDirectory())
				throw new ArgumentsException("Path to model contains a runs file");
			
			// Select random runs folder
			
			File randomRunsFolder = new File(runsFolder, "random");
			
			if (!randomRunsFolder.exists())
				randomRunsFolder.mkdir();
			else if (!randomRunsFolder.isDirectory())
				throw new ArgumentsException("Path to model runs contains a random file");
			
			// Select greedy runs folder
			
			File greedyRunsFolder = new File(runsFolder, "greedy");
			
			if (!greedyRunsFolder.exists())
				greedyRunsFolder.mkdir();
			else if (!greedyRunsFolder.isDirectory())
				throw new ArgumentsException("Path to model runs contains a greedy file");
			
			// Select smart runs folder
			
			File smartRunsFolder = new File(runsFolder, "smart");
			
			if (!smartRunsFolder.exists())
				smartRunsFolder.mkdir();
			else if (!smartRunsFolder.isDirectory())
				throw new ArgumentsException("Path to model runs contains a smart file");
			
			// Create parser
			
			Parser parser = new Parser();
			
			// Parse models
			
			Model model1 = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "stations.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			Model model2 = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "stations.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			Model model3 = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "stations.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			
			// Clear demands
			
			model1.demands.clear();
			model2.demands.clear();
			model3.demands.clear();
			
			// List models
			
			List<Model> models = new ArrayList<>();
			
			models.add(model1);
			models.add(model2);
			models.add(model3);
			
			// Generate demands
			
			for (int index = 0; index < 100; index++) {
				// Select size
				
				double size = Math.random() * 4;
				
				// Select times
				
				double pickupTime = Math.random() * 1000000;
				double dropoffTime = pickupTime + Math.random() * 100000;
				
				// Select segments
				
				int pickupSegmentNumber = (int) (Math.random() * model1.segments.size());
				int dropoffSegmentNumber = (int) (Math.random() * model1.segments.size());
				
				// Select distances
				
				double pickupDistance = Math.random();
				double dropoffDistance = Math.random();
				
				// Check segment validity
				
				boolean valid = true;
				
				for (Model model : models) {
					for (Station station : model.stations) {
						if (station.location.segment == model.segments.get(pickupSegmentNumber)) {
							valid = false;
						} else if (station.location.segment == model.segments.get(dropoffSegmentNumber)) {
							valid = false;
						}
						if (!valid) {
							break;
						}
					}
					if (!valid) {
						break;
					}
				}
				if (!valid) {
					index--;
					continue;
				}
				
				// Process models
				
				for (Model model : models) {
					Segment pickupSegment = model.segments.get(pickupSegmentNumber);
					Segment dropoffSegment = model.segments.get(dropoffSegmentNumber);
					
					Demand demand = new Demand(pickupSegment, pickupDistance * pickupSegment.getLength(), pickupTime, dropoffSegment, dropoffDistance * dropoffSegment.getLength(), dropoffTime, size);

					model.demands.add(demand);
				}
			}
			
			// Sort demands and reset models
			
			for (Model model : models) {
				model.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
				
				model.reset();
			}
			
			// Create controllers
			
			Controller controller1 = new RandomController();
			Controller controller2 = new GreedyController(model2);
			Controller controller3 = new SmartController(model3);
			
			controller1.reset();
			controller2.reset();
			controller3.reset();
			
			// Create statistics
			
			ExampleStatistics statistics1 = new ExampleStatistics(model1);
			ExampleStatistics statistics2 = new ExampleStatistics(model2);
			ExampleStatistics statistics3 = new ExampleStatistics(model3);
			
			statistics1.reset();
			statistics2.reset();
			statistics3.reset();
			
			// Create synchronizer
			
			Synchronizer synchronizer = new Synchronizer(3);
			
			// Define settings
			
			double maxModelTimeStep = 1000;
			double ratioModelRealTime = 30;
			
			// Create simulators
			
			Simulator<ExampleStatistics> simulator1 = new Simulator<>("Random", model1, controller1, statistics1, maxModelTimeStep, ratioModelRealTime, randomRunsFolder, synchronizer);
			Simulator<ExampleStatistics> simulator2 = new Simulator<>("Greedy", model2, controller2, statistics2, maxModelTimeStep, ratioModelRealTime, greedyRunsFolder, synchronizer);
			Simulator<ExampleStatistics> simulator3 = new Simulator<>("Smart", model3, controller3, statistics3, maxModelTimeStep, ratioModelRealTime, smartRunsFolder, synchronizer);
			
			// List simulators
			
			List<Simulator<ExampleStatistics>> simulators = new ArrayList<>();
			
			simulators.add(simulator1);
			simulators.add(simulator2);
			simulators.add(simulator3);
			
			// Create viewer
			
			new MultipleViewer(simulators);
			
			// Start simulators
			
			simulator1.start();
			simulator2.start();
			simulator3.start();
		} catch (MissingException e) {
			e.printStackTrace();
		} catch (DirectoryException e) {
			e.printStackTrace();
		} catch (ArgumentsException e) {
			e.printStackTrace();
		}
	}
	
}
