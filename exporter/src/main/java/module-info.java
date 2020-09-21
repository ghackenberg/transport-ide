module example.exporter {
	
	requires transitive example.model;
	requires transitive example.statistics;
	
	exports example.exporter;
	exports example.exporter.implementations;
	
}