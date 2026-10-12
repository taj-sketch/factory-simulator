package factorysim;

import factorysim.config.ConfigParseException;
import factorysim.config.ConfigParser;
import factorysim.config.SimulatorConfig;
import factorysim.model.BeltValidationException;
import factorysim.simulation.Report;
import factorysim.simulation.Simulator;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Main entry point for the factory simulation program.
 *
 * Usage: java --class-path src/main/java factorysim.Main examples/<config-file>
 *
 * Reads the specified configuration file, runs the simulation, and prints
 * the resulting report to standard output. Errors during file reading,
 * parsing, or simulation are printed to standard error and cause a non-zero
 * exit code.
 */
public final class Main {

    private Main() {}

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: factorysim.Main <config-file>");
            System.exit(1);
        }

        try {
            SimulatorConfig config = ConfigParser.parse(Path.of(args[0]));
            Simulator simulator = new Simulator(config);
            simulator.run();
            Report report = simulator.generateReport();
            System.out.print(report);
        } catch (IOException e) { // thrown by config parse.
            System.err.println("Error reading config file: " + e.getMessage());
            System.exit(1);
        } catch (ConfigParseException e) { // thrown by config parser
            System.err.println("Error parsing config file: " + e.getMessage());
            System.exit(1);
        } catch (BeltValidationException e) { // thrown by FactoryNetwork
            System.err.println("Invalid factory design due to belt validation: " + e.getMessage());
            System.exit(1);
        }
    }
}
