# Factory Simulator

A Java simulation of a production network: machines consume and produce materials, conveyor belts distribute output, and sinks record deliveries. Developed as an individual UWA CITS2005 object-oriented programming project.

## Implementation

- Machines with input and output buffers, processing delays and blocked-output handling.
- Conveyor networks with balanced merging and splitting.
- Tick-based simulation, settling periods and production statistics.
- Configuration-driven factories, example networks and expected-output fixtures.
- Unit and integration tests for machine behaviour, sinks and complete factories.

## Run

Use JDK 21 or newer and Bash. The JUnit console dependency is included in `lib/`.

```bash
shopt -s globstar
mkdir -p build/main build/test
javac -d build/main src/main/java/**/*.java
java -cp build/main factorysim.Main examples/concrete_factory.cfg
```

Other example factories are in `examples/`.

## Test

```bash
javac -cp "build/main:lib/junit-platform-console-standalone-6.0.3.jar" \
  -d build/test src/test/java/**/*.java
java -jar lib/junit-platform-console-standalone-6.0.3.jar execute \
  --class-path "build/main:build/test:src/test/resources" --scan-class-path
```

The existing suite contains 119 tests. All 119 passed during repository preparation using Java 17 with preview enabled for the supplied configuration parser. On Java 17, add `--enable-preview --release 17` to each `javac` command and `--enable-preview` to each `java` command.

For Windows, the original `build.bat` and `buildtest.bat` scripts and [course build instructions](docs/course-readme.md) are included.

## Files and attribution

`src/main/` contains the application, `src/test/` contains tests and fixtures, and `examples/` contains runnable configurations. The original project specification is retained as `project_specification.pdf`.

This repository preserves the completed project, including supplied UWA course scaffolding for configuration parsing, simulation and statistics. The factory-model implementation and additional tests are part of the submitted individual project.
