module example.simulator {
	
	requires transitive example.model;
	requires transitive example.controller;
	requires transitive example.statistics;
	
	exports example.simulator;
	exports example.simulator.exceptions;
	
}