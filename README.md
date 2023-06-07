# TransportIDE

The goal of **TransportIDE** is to make the life of *transportation infrastructure* and *control algorithm engineers* easier.
We believe that this engineering domain needs new tools and methodologies to develop better systems with higher performance in shorter time and at lower lifetime cost.
In particular, we think that transportation system engineers must be supported better in

1. analyzing requirements for specific application scenarios (e.g. travel time and energy consumption) as well as
2. deriving, verifying, and validating principle solution variants (including transportation, charge, and maintenance infrastructure as well as control strategy).

**TransportIDE** is an *open source software project* initiated by Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, [University of Applied Sciences Upper Austria](https://fh-ooe.at/).

## Screenshots

This software package supports several use cases from basic simulation to controller and infrastructure comparison.
In the following, we provide brief descriptions and screenshots of the individual use cases.

### 🖼️ Basic simulation

Basic simulation allows one to evaluate system performance for a given controller algorithm and transportation infrastructure.

![Basic](./screenshots/basic-simulation.png)

### 🖼️ Controller comparison

Controller comparison allows one to evaluate system performance for a set of controller algorithms and a given transportation infrastructure.

![Controller comparison](./screenshots/controller-comparison.png)

### 🖼️ Infrastructure comparison

Infrastructure comparison allows one to evaluate system performance for a given controller algorithmn and a set of transportation infrastructures.

![Infrastructure comparison](./screenshots/infrastructure-comparison.png)

## Requirements

To build and run the project you need to following software packages:
![Alt text](https://file%2B.vscode-resource.vscode-cdn.net/c%3A/Users/P28500/Desktop/Repositories/Forschung/transport-ide/diagrams/architecture.svg)
- [OpenJDK](https://openjdk.org/) provides the Java compiler and runtime environment
- [Apache Maven](https://maven.apache.org/) provides dependency and build management

To calculate fast routes for your vehicles you need the following software package:

- [JGraphT](https://jgrapht.org/) provides graph algorithms (such as shortest path)

To display the simulation state and performance statistics you need the following software packages:

- [DockingFrames](https://www.docking-frames.org/) provides GUI docking components
- [JFreeChart](https://www.jfree.org/jfreechart/) provides GUI charting components

## Modules

This software packages comprises a number of modules and their dependencies.
The following diagram provides an overview of the software modules included.
Technically, the software modules are implemented as Java Jigsaw and Apache Maven modules.

![Model classes](./diagrams/architecture.svg)

### 🧩 Model

The model represents the core of the application and defines the concepts needed for transportation system design.
We have developed three versions of the model, while only the first version is implemented in the software today.
The other versions of the model are subject to future work.

#### First version

The first version of the model allows one to define road infrastructures, charge infrastructures, vehicles, and transportation demands.

![Model classes](./diagrams/model/classes-v0.svg)

#### Second version

The second version of the model allows one to define more complex transportation demands including chains of transportation activities.

![Model classes](./diagrams/model/classes-v1.svg)

#### Third version

The third versiob of the model allows one to define even more complex transportation demands including parameterizable objects and operations.

![Model classes](./diagrams/model/classes-v2.svg)

### 🧩 Controller

The controller interface is responsible for plugging different control algorithms into the simulation engine.
Through this mechanism, the simulation engine is decoupled from the control strategies, that drive the system behavior.
The module also contains different implementations of the controller interface such as a random controller or a JGraphT-based controller.

![Controller classes](./diagrams/controller/classes.svg)

### 🧩 Statistics

The statistics interface is responsible for collecting performance data during simulation experiments.
The performance data is necessary to compare infrastructure and control algorithm designs in specific situations.
The interface assumes that performance data is collected only in specific events such as a vehicle passing a road crossing.

![Statistics classes](./diagrams/statistics/classes.svg)