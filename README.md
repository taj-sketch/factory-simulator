# Factory Simulator

A discrete-time Java simulator for factory production. Define machines and conveyor connections, then measure machine utilisation, material flow and finished-goods output.

**Java 21+ · JUnit · Object-oriented modelling · 119 tests**

## What it models

- **Machines:** input and output buffers, batch production, cooldowns and blocked outputs.
- **Conveyors:** round-robin merging and splitting across multiple sources and destinations.
- **Factory networks:** configuration-based wiring and validation of material types and sink connections.
- **Statistics:** a warmup period followed by machine utilisation, belt throughput and sink delivery rates.

Each simulated second has two phases: machines update on a **tick**, then conveyors and the sink transfer items on a **tock**. Resetting statistics keeps the factory's stored materials and conveyor turn order intact.

## Quick start

Requires JDK 21 or newer. The JUnit console runner is included in `lib/`; no dependency download is needed.

Clone the repository, then run these commands from its root in a Linux/macOS Bash shell or WSL:

```bash
git clone https://github.com/taj-sketch/factory-simulator.git
cd factory-simulator
shopt -s globstar
mkdir -p build/main build/test
javac --release 21 -d build/main src/main/java/**/*.java
java -cp build/main factorysim.Main examples/concrete_factory.cfg
```

The concrete factory example reports machine utilisation, belt throughput and a finished output of **400 concrete items per minute**. Other configurations model [electric motors](examples/electric_motors.cfg) and an [oil refinery](examples/oil_refinery.cfg).

```text
=== Sink ===
  Concrete:            400.0 / min
```

## Run the tests

After compiling the application:

```bash
javac --release 21 \
  -cp "build/main:lib/junit-platform-console-standalone-6.0.3.jar" \
  -d build/test src/test/java/**/*.java
java -jar lib/junit-platform-console-standalone-6.0.3.jar execute \
  --class-path "build/main:build/test:src/test/resources" \
  --scan-class-path
```

The suite contains **119 passing tests**, covering machine activation and cooldowns, sink statistics, balanced and uneven conveyor networks, backpressure, cycles, statistic resets and invalid configurations.

<details>
<summary>Windows PowerShell commands</summary>

Compile and run:

```powershell
New-Item -ItemType Directory -Force build/main, build/test | Out-Null
$mainSources = (Get-ChildItem src/main/java -Recurse -Filter *.java).FullName
javac --release 21 -d build/main $mainSources
java -cp build/main factorysim.Main examples/concrete_factory.cfg
```

Compile and run the tests:

```powershell
$testSources = (Get-ChildItem src/test/java -Recurse -Filter *.java).FullName
javac --release 21 -cp "build/main;lib/junit-platform-console-standalone-6.0.3.jar" -d build/test $testSources
java -jar lib/junit-platform-console-standalone-6.0.3.jar execute --class-path "build/main;build/test;src/test/resources" --scan-class-path
```

</details>

## Code guide

| Location | Responsibility |
| --- | --- |
| [`model/`](src/main/java/factorysim/model) | Machines, input/output ports, conveyor balancing, network wiring and the sink |
| [`config/`](src/main/java/factorysim/config) | Parse factory configuration files |
| [`simulation/`](src/main/java/factorysim/simulation) | Coordinate tick/tock updates and generate reports |
| [`stats/`](src/main/java/factorysim/stats) | Statistics data objects |
| [`src/test/`](src/test) | Unit tests, integration configurations and expected reports |
| [`examples/`](examples) | Runnable factory configurations |

## Project and contribution

Individual **CITS2005 Object Oriented Programming** project at the University of Western Australia, completed by **Surtaj Singh**.

My work covers the factory-model implementation: machine behaviour, input/output ports, conveyor balancing, sink behaviour, network wiring and validation, plus additional model tests. UWA supplied the configuration parser, simulation and reporting framework, interfaces and course test material.

[Course requirements](project_specification.pdf) · [Course build and structure guide](docs/course-readme.md)
