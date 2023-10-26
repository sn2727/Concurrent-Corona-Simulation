# Concurrent Pandemic Simulation
Concurrent simulation of disease spreading. Implemented in the concurrent programming course at Saarland University.

#### Small scale example:
![](./scenarios/example.gif)

Boxes represent people, orange = incubation, red = infected and contagious to nearby people, blue = healthy, green = immune (after infection) 

### Structure 

- `src/main/java/com/pseuco/np20/`: Java source code of the project.
    - `model/`: Data structures for the simulation.
    - `simulation/rocket/`: Concurrent implementation.
    - `simulation/slug/`: Sequential reference implementation.
    - `validator/`: The validator interface.
    - `Simulation.java`: Implements the `main` method.
- `src/test`: Public tests for the project.
- `scenarios`: Some example scenarios.


### Gradle
[Gradle](https://gradle.org/) is used to build the project.

To build the Javadoc run:
```bash
./gradlew javaDoc
```
Afterwards you find the documentation in `build/docs`.


To build a `simulation.jar`-File for your project run:
```bash
./gradlew jar
```
You find the compiled `.jar`-File in `out`.

To run the *public* tests on your project run:
```bash
./gradlew test
```