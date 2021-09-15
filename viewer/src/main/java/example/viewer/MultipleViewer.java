package example.viewer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import example.simulator.Simulator;
import example.simulator.exceptions.InvalidException;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.charts.DemandTimesChartViewer;

public class MultipleViewer {
	
	private static final int GAP = 10;
	
	private final JFrame frame;
	private final JPanel border;
	private final DockController controller;
	private final SplitDockStation station;
	private final SplitDockGrid grid;
	
	private final List<Viewer> viewers = new ArrayList<>();
	
	public MultipleViewer(List<Simulator<ExampleStatistics>> simulators) {
		// Create grid
		grid = new SplitDockGrid();
		
		// FIXME Remove work around for linux
		System.setProperty("java.version", "13.0.0");
		
		// Create station
		station = new SplitDockStation();
		station.dropTree(grid.toTree());
		
		// Create controller
		controller = new DockController();
		controller.add(station);
		
		// Create panel
		border = new JPanel();
		border.setLayout(new BorderLayout(GAP, GAP));
		border.add(station.getComponent(), BorderLayout.CENTER);
		
		// Create frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setResizable(true);
		frame.setTitle("Multiple Viewer");
		frame.setContentPane(border);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
		// Process simulators
		int index = 0;
		
		for (Simulator<ExampleStatistics> simulator : simulators) {
			final int number = index;
			
			addViewer(number, 0, 1, 1, new ModelViewer(simulator.getModel(), simulator.getStatistics()));
			addViewer(number, 1, 1, 1, new DemandTimesChartViewer(simulator.getModel(), simulator.getStatistics()));
			
			simulator.setHandleUpdated(() -> {
				handleUpdated(number * 2 + 0);
				handleUpdated(number * 2 + 1);
			});
			simulator.setHandleStopped(() -> {
				handleStopped(number);
			});
			simulator.setHandleFinished(() -> {
				handleFinished(number);
			});
			simulator.setHandleException(exception -> {
				handleException(number, exception);
			});
			
			index++;
		}
	}
	
	private void addViewer(double x, double y, double width, double height, Viewer viewer) {
		viewers.add(viewer);
		
		grid.addDockable(x, y, width, height, new DefaultDockable(viewer.getComponent(), viewer.getName(), viewer.getIcon()));
		
		station.dropTree(grid.toTree());
	}
	
	private void handleUpdated(int index) {
		viewers.get(index).update();
	}
	
	private void handleStopped(int index) {
		//JOptionPane.showMessageDialog(frame, "Simulator stopped", "Stopped (" + index + ")", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void handleFinished(int index) {
		//JOptionPane.showMessageDialog(frame, "Simulator finished", "Finished (" + index + ")", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void handleException(int index, InvalidException exception) {
		JOptionPane.showMessageDialog(frame, exception.getMessage(), "Exception (" + index + ")", JOptionPane.WARNING_MESSAGE);
	}
	
}
