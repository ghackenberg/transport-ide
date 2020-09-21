module example.controller {
	
	requires transitive example.model;
	requires transitive java.desktop;
	requires org.jgrapht.core;
	requires org.jheaps;
	
	exports example.controller;	
	exports example.controller.implementations;
	
}