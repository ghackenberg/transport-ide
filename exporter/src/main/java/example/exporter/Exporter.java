package example.exporter;

import example.model.Model;
import example.statistics.Statistics;

public interface Exporter<T extends Statistics> {

	public void export(Model model, T statistics);
	
}
