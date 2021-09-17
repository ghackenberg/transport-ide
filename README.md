# transport-ide

Transport IDE provides a tool for transportation system designers to analyse requirements (e.g. travel time, energy consumption) as well as to derive, verify, and validate concepts (e.g. transportation infrastructure, control strategy).

## Diagrams

### Model

```mermaid
classDiagram
    direction LR
    class Intersection {
        Name: String
        Latitude: Double
        Longitude: Double
        Elevation: Double
    }
    class Segment {
        Speed: Double
        Lanes: Double
        GetLength() Double
    }
    class Location {
        Distance: Double
    }
    class LocationTime {
        Time: Double
    }
    class Station {

    }
    class Vehicle {
        BatteryCapacity: Double
        BatteryLevel: Double
        DemandCapacity: Double
        DemandLevel: Double
        Speed: Double
    }
    class Demand {
        Size: Double
    }
    Segment --> "1" Intersection:Start
    Segment --> "1" Intersection:End
    Location --> "1" Segment
    LocationTime --> "1" Location
    Station --> "1" Location
    Vehicle --> "1" Location
    Demand --> "1" LocationTime:Pickup
    Demand --> "1" LocationTime:Dropoff
```

### Controller

```mermaid
classDiagram
    direction LR
    class Controller {
        SelectDemand(Vehicle v, Demand d) Boolean
        SelectStation(Vehicle v, Station s) Boolean
        UnselectStation(Vehicle v) Boolean
        SelectSegment(Vehicle v) Segment
        SelectSpeed(Vehicle v) Double
        SelectMaximumSpeedSelectionTimeout(Vehicle v) Double
        SelectMaximumStationSelectionTimeout(Vehicle v) Double
    }
    class RandomController {

    }
    class GreedyController {

    }
    class SmartController {
        GetMinimumDistance(Location a, Location b) Double
        GetMinimumStationDistance(Location l) Double
    }
    class ManualController {

    }
    class JGraphT {
        <<library>>
    }
    class Swing {
        <<library>>
    }
    RandomController --|> Controller
    GreedyController --|> Controller
    SmartController --|> Controller
    ManualController --|> Controller
    
    SwitchableController --|> Controller
    SwitchableController --> "*" Controller:Controllers
    SwitchableController --> "1" Controller:Active

    JGraphT <-- SmartController
    Swing <-- ManualController
```