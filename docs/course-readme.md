# README: FACTORY Project

This is the readme for the factory simulation CITS2005 2026 project. 

## Compiling and Running the Project

### Linux/WSL/Mac (Recommended)

Your code will be tested using these instructions. It is strongly recommended you use WSL or a suitable
bash shell (such as GitBash) on Windows with these instructions.

These are all to be run from project root directory (`factoryproject`).

Compile application:

```
javac src/main/**/*.java
```

Run application:

```
java --class-path src/main/java factorysim.Main examples/concrete_factory.cfg
```

Compile tests:

```
javac --class-path src/main/java:lib/junit-platform-console-standalone-6.0.3.jar src/test/**/*.java
```

Run tests:

```
java -jar lib/junit-platform-console-standalone-6.0.3.jar execute --class-path src/main/java:src/test/java:src/test/resources --scan-class-path
```

### Enable globstar

To use **/*.java you must have enabled the globstar shell option (default value is off). 

You can check if it's already enabled via the command in the terminal: ```shopt globstar```

To enable it: ```shopt -s globstar```

To disable it: ```shopt -u globstar```

Note that it needs to be enabled every time you open a new terminal (or you can add it to your .bashrc file - but modifying this is out of scope for the course!)

### Windows Scripts

Windows does not support globstar, so the compile commands will not work, even with the usual alterations!

You can either:
(1) Adapt to Windows as usual, but you'll have to compile each folder individually, e.g.:
```
javac src/main/java/factorysim/config/*.java
```
(2) Two very basic .bat (executable windows cmd script files) have been provided, which compile recursively.
You can run these from the project root directory in the windows command line
- To compile the application: ```build.bat```
- To compile the tests: ```buildtest.bat```

Then run the application/tests using the typical changes:

```
java --class-path src\main\java factorysim.Main examples\concrete_factory.cfg
```

```
java -jar lib\junit-platform-console-standalone-6.0.3.jar execute --class-path src\main\java;src\test\java;src\test\resources --scan-class-path
```

## Overview of Project Structure:

The project is structured as follows
- project specification pdf (also available on LMS)
- Windows build files
- examples: contains example configuration (.cfg) files which can be used to run your application
- lib: contains testing jar to use
- src: primary project folder
  - main/java/factorysim: top-level package
    - config: Contains all the (provided) code for parsing a configuration file
    - model: The folder that should contain all the classes you implement. A skeleton Sink and FactoryNetworkImpl class is provided, and an empty Machine class, along with some useful interfaces. YOUR CODE GOES HERE.
    - simulation: Contains the provided Simulator class, and Report class. 
    - stats: Contains the immutable data objects representing useful statistics on factory components for data transfer from simulation to report
    - Main: The entry point for running the application.
  - test: The test folder
    - java/factorysim: top-level package
      - model: tests for the factorysim.model package classes. This currently only includes the Sink class. YOUR CODE GOES HERE
      - IntegrationTest.java: The integration tests we'll run to test your implementation
    - resources: Resources for testing (e.g. sample expected reports and configuration files)
