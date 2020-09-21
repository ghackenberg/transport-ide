package example.program;

import java.io.File;

import example.controller.Controller;
import example.controller.implementations.GreedyController;
import example.controller.implementations.RandomController;
import example.controller.implementations.SmartController;
import example.model.Demand;
import example.model.Model;
import example.model.Segment;
import example.parser.Parser;
import example.parser.exceptions.DirectoryException;
import example.parser.exceptions.MissingException;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.simulator.Synchronizer;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.MultipleViewer;

public class MultipleProgram {

	public static void main(String[] args) {
		try {
			double maxModelTimeStep = 1000;
			double ratioModelRealTime = 30;
			
			if (args.length < 1)
				throw new ArgumentsException("Not enough arguments: <path/to/model>");
		
			File modelFolder = new File(args[0]);
			
			if (!modelFolder.exists() || !modelFolder.isDirectory())
				throw new ArgumentsException("Path to model is not a folder");
			
			File runsFolder = new File(modelFolder, "runs");
			
			if (!runsFolder.exists())
				runsFolder.mkdir();
			else if (!runsFolder.isDirectory())
				throw new ArgumentsException("Path to model contains a runs file");
			
			Parser parser = new Parser();
			
			Model model1 = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			Model model2 = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			Model model3 = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			
			model1.demands.clear();
			model2.demands.clear();
			model3.demands.clear();
			
			for (int index = 0; index < 100; index++) {
				double size = Math.random() * 4;
				
				int pickupSegment = (int) (Math.random() * model1.segments.size());
				int dropoffSegment = (int) (Math.random() * model1.segments.size());
				
				Segment pickupSegment1 = model1.segments.get(pickupSegment);
				Segment pickupSegment2 = model2.segments.get(pickupSegment);
				Segment pickupSegment3 = model3.segments.get(pickupSegment);
				
				Segment dropoffSegment1 = model1.segments.get(dropoffSegment);
				Segment dropoffSegment2 = model2.segments.get(dropoffSegment);
				Segment dropoffSegment3 = model3.segments.get(dropoffSegment);
				
				double pickupTime = Math.random() * 1000000;
				double dropoffTime = pickupTime + Math.random() * 100000;
				
				double pickupDistance = Math.random() * pickupSegment1.getLength();
				double dropoffDistance = Math.random() * dropoffSegment1.getLength();
				
				Demand demand1 = new Demand(pickupSegment1, pickupDistance, pickupTime, dropoffSegment1, dropoffDistance, dropoffTime, size);
				Demand demand2 = new Demand(pickupSegment2, pickupDistance, pickupTime, dropoffSegment2, dropoffDistance, dropoffTime, size);
				Demand demand3 = new Demand(pickupSegment3, pickupDistance, pickupTime, dropoffSegment3, dropoffDistance, dropoffTime, size);
				
				model1.demands.add(demand1);
				model2.demands.add(demand2);
				model3.demands.add(demand3);
			}
			
			model1.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
			model2.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
			model3.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
			
			model1.reset();
			model2.reset();
			model3.reset();
			
			Controller controller1 = new RandomController();
			Controller controller2 = new GreedyController(model2);
			Controller controller3 = new SmartController(model3);
			
			controller1.reset();
			controller2.reset();
			controller3.reset();
			
			ExampleStatistics statistics1 = new ExampleStatistics(model1);
			ExampleStatistics statistics2 = new ExampleStatistics(model2);
			ExampleStatistics statistics3 = new ExampleStatistics(model3);
			
			statistics1.reset();
			statistics2.reset();
			statistics3.reset();
			
			Synchronizer synchronizer = new Synchronizer(3);
			
			Simulator<ExampleStatistics> simulator1 = new Simulator<>(model1, controller1, statistics1, maxModelTimeStep, ratioModelRealTime, new File("models/basic/runs"), synchronizer);
			Simulator<ExampleStatistics> simulator2 = new Simulator<>(model2, controller2, statistics2, maxModelTimeStep, ratioModelRealTime, new File("models/basic/runs"), synchronizer);
			Simulator<ExampleStatistics> simulator3 = new Simulator<>(model3, controller3, statistics3, maxModelTimeStep, ratioModelRealTime, new File("models/basic/runs"), synchronizer);
			
			new MultipleViewer(simulator1, simulator2, simulator3);
			
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
