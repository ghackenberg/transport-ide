package example.program;

import java.io.File;

import example.controller.implementations.GreedyController;
import example.controller.implementations.ManualController;
import example.controller.implementations.RandomController;
import example.controller.implementations.SmartController;
import example.controller.implementations.SwitchableController;
import example.exporter.Exporter;
import example.exporter.implementations.CSVExporter;
import example.model.Model;
import example.parser.Parser;
import example.parser.exceptions.DirectoryException;
import example.parser.exceptions.MissingException;
import example.program.dialogs.ModelOpenDialog;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ModelViewer;
import example.viewer.SingleViewer;
import example.viewer.charts.DemandDistancesChartViewer;
import example.viewer.charts.DemandTimesChartViewer;
import example.viewer.charts.IntersectionCrossingsChartViewer;
import example.viewer.charts.SegmentTraversalsChartViewer;
import example.viewer.charts.VehicleDistancesChartViewer;

public class StaticProgram {

	public static void main(String[] args) {
		
		System.out.println("SingleProgram.main");
		
		try {
			File modelFolder = ModelOpenDialog.choose();
			
			if (modelFolder == null) {
				return;
			}
			
			File runsFolder = new File(modelFolder, "runs");
			
			if (!runsFolder.exists())
				runsFolder.mkdir();
			else if (!runsFolder.isDirectory())
				throw new ArgumentsException("Path to model contains a runs file");
			
			// Create parser
			Parser parser = new Parser();
			// Parser model
			Model model = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			model.reset();
			// Create controller
			SwitchableController controller = new SwitchableController();
			controller.addController(new RandomController());
			controller.addController(new ManualController());
			controller.addController(new GreedyController(model));
			controller.addController(new SmartController(model));
			controller.reset();
			// Create statistics
			ExampleStatistics statistics = new ExampleStatistics(model);
			statistics.reset();
			// Create simulator
			Simulator<ExampleStatistics> simulator = new Simulator<>(model, controller, statistics, 1000.0 / 30.0, 1.0, runsFolder);
			// Create exporter
			Exporter<ExampleStatistics> exporter = new CSVExporter(".");
			// Create viewer
			SingleViewer<ExampleStatistics> viewer = new SingleViewer<>(simulator, controller);
			viewer.addViewer(0, 0, 1, 1, new ModelViewer(model, statistics));
			viewer.addViewer(0, 1, 1, 1, new DemandTimesChartViewer(model, statistics));
			viewer.addViewer(1, 0, 1, 1, new VehicleDistancesChartViewer(model, statistics));
			viewer.addViewer(1, 1, 1, 1, new DemandDistancesChartViewer(model, statistics));
			viewer.addViewer(2, 0, 1, 1, new SegmentTraversalsChartViewer(model, statistics));
			viewer.addViewer(2, 1, 1, 1, new IntersectionCrossingsChartViewer(model, statistics));
			// Start simulator 
			simulator.setHandleUpdated(() -> {
				viewer.handleUpdated();
			});
			simulator.setHandleStopped(() -> {
				viewer.handleStopped();
				exporter.export(model, statistics);
			});
			simulator.setHandleFinished(() -> {
				viewer.handleFinished();
				exporter.export(model, statistics);
			});
			simulator.setHandleException(exception -> {
				viewer.handleException(exception);
			});
			simulator.start();
			// Print time
			System.out.println("Finished in " + Math.round(model.time)+ "ms");
		} catch (ArgumentsException exception) {
			
			System.err.println(exception.getMessage());
			
		} catch (MissingException exception) {
			
			System.err.println(exception.getMessage());
			
		} catch (DirectoryException exception) {
			
			System.err.println(exception.getMessage());
			
		}
		
	}

}
