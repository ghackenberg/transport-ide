package example.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import example.controller.Controller;
import example.controller.implementations.SmartController;
import example.model.Demand;
import example.model.Model;
import example.model.Segment;
import example.parser.Parser;
import example.parser.exceptions.DirectoryException;
import example.parser.exceptions.MissingException;
import example.program.dialogs.ModelOpenDialog;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.simulator.Synchronizer;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.MultipleViewer;

public class ModelComparisonProgram {

	public static void main(String[] args) {
		try {
			double maxModelTimeStep = 1000;
			double ratioModelRealTime = 30;
			
			List<File> folders = new ArrayList<>();
			
			List<Model> models = new ArrayList<>();
			
			do {
				File modelFolder = ModelOpenDialog.choose();
				
				if (modelFolder == null) {
					break;
				}

				File runsFolder = new File(modelFolder, "runs");
				
				if (!runsFolder.exists())
					runsFolder.mkdir();
				else if (!runsFolder.isDirectory())
					throw new ArgumentsException("Path to model contains a runs file");

				File indexRunsFolder = new File(runsFolder, "run-" + folders.size());
				
				if (!indexRunsFolder.exists())
					indexRunsFolder.mkdir();
				else if (!indexRunsFolder.isDirectory())
					throw new ArgumentsException("Path to model runs contains a run-" + folders.size() + " file");
				
				Parser parser = new Parser();
				
				Model model = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "stations.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
				
				model.demands.clear();
				
				folders.add(indexRunsFolder);
				
				models.add(model);
			} while (true);
			
			for (int index = 0; index < 100; index++) {
				double size = Math.random() * 4;
				
				double pickupTime = Math.random() * 1000000;
				double dropoffTime = pickupTime + Math.random() * 100000;
				
				int pickupSegmentNumber = (int) (Math.random() * models.get(0).segments.size());
				int dropoffSegmentNumber = (int) (Math.random() * models.get(0).segments.size());
				
				for (Model model : models) {
					Segment pickupSegment = model.segments.get(pickupSegmentNumber);
					Segment dropoffSegment = model.segments.get(dropoffSegmentNumber);
					
					double pickupDistance = Math.random() * pickupSegment.getLength();
					double dropoffDistance = Math.random() * dropoffSegment.getLength();
					
					Demand demand = new Demand(pickupSegment, pickupDistance, pickupTime, dropoffSegment, dropoffDistance, dropoffTime, size);
					
					model.demands.add(demand);
				}
			}
			
			for (Model model : models) {
				model.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
				
				model.reset();
			}
			
			List<Controller> controllers = new ArrayList<>();
			
			List<ExampleStatistics> statistics = new ArrayList<>();
			
			List<Simulator<ExampleStatistics>> simulators = new ArrayList<>();
			
			Synchronizer synchronizer = new Synchronizer(models.size());
			
			for (int index = 0; index < models.size(); index++) {
				File runsFolder = folders.get(index);
				
				Model model = models.get(index);
				
				// Controller
				
				Controller controller = new SmartController(model);
				controller.reset();
				
				controllers.add(controller);
				
				// Statistics

				ExampleStatistics statistic = new ExampleStatistics(model);
				statistic.reset();
				
				statistics.add(statistic);
				
				// Simulator
				
				Simulator<ExampleStatistics> simulator = new Simulator<>(model, controller, statistic, maxModelTimeStep, ratioModelRealTime, runsFolder, synchronizer);
				
				simulators.add(simulator);
			}
			
			new MultipleViewer(simulators);
			
			for (Simulator<ExampleStatistics> simulator : simulators) {
				simulator.start();
			}
		} catch (MissingException e) {
			e.printStackTrace();
		} catch (DirectoryException e) {
			e.printStackTrace();
		} catch (ArgumentsException e) {
			e.printStackTrace();
		}
	}
	
}
