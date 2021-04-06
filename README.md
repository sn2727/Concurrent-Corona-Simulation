# Concurrent Pandemic Simulation
Reference implementation and framework for the concurrent programming project 2020.

## Structure
This project is structured as follows:

- `src/main/java/com/pseuco/np20/`: Java source code of the project.
    - `model/`: Data structures for the simulation.
    - `simulation/rocket/`: Your implementation goes here.
    - `simulation/common/`: Simulation functionality you might find useful.
    - `simulation/slug/`: The sequential reference implementation.
    - `validator/`: The validator interface.
    - `Simulation.java`: Implements the `main` method.
- `src/test`: Public tests for the project.
- `scenarios`: Some example scenarios.


## Gradle
We use [Gradle](https://gradle.org/) to build the project.

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


## Integrated Development Environment
We recommend you use a proper *Integrated Development Environment* (IDE) for this project.
A good choice you should already be familiar with from *Programming* 2 is [Eclipse](https://www.eclipse.org/).
Another good open source IDE is [VS Code](https://code.visualstudio.com/).
While Eclipse is more focused on Java and provides a better experience when it comes to Java programming, VS Code is more universal and might be worth using as a general editor during your studies for all kinds of tasks like writing your bachelor thesis.
Which IDE or editor you use is up to you.
However, we only provide help for Eclipse and VS Code.
In case you use something else, do not expect help.

### Visual Studio Code
In case you decide to use VS Code, we recommend installing the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and the [Gradle Extension Pack](https://marketplace.visualstudio.com/items?itemName=richardwillis.vscode-gradle-extension-pack).
