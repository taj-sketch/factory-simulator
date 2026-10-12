// DO NOT MODIFY THIS FILE
package factorysim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import factorysim.config.ConfigParser;
import factorysim.config.SimulatorConfig;
import factorysim.model.BeltValidationException;
import factorysim.stats.*;
import factorysim.simulation.Report;
import factorysim.simulation.Simulator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Integration tests that discover .cfg/.out pairs in the resources/integration
 * directory and verify that running the simulator on each .cfg file produces
 * output matching the corresponding .out file.
 *
 * <p>The expected .out files are parsed using {@link Report#parse(String)} and
 * compared structurally against the simulator's actual report. Numeric values
 * are compared with a small tolerance and the order of entries within each
 * section does not matter.
 *
 * <p>To add a new test case, simply add a {@code test_name.cfg} config file
 * and its expected {@code test_name.out} output file to
 * {@code src/test/resources/integration/}.
 */
public class IntegrationTest {

    private static final String INTEGRATION_DIR = "src/test/resources/integration";
    private static final String INVALID_INTEGRATION_DIR =
        "src/test/resources/integration/invalid";

    /**
     * Absolute tolerance for comparing utilisation percentages (in percentage
     * points, e.g. 0.1 means ±0.1%).
     */
    private static final double UTILISATION_TOLERANCE_PCT = 0.15;

    /**
     * Absolute tolerance for comparing throughput values (items per minute).
     */
    private static final double THROUGHPUT_TOLERANCE = 0.15;

    /**
     * Discovers all .cfg files in the integration resources directory and
     * returns their base names (without extension) as test case identifiers.
     */
    static Stream<String> testCases() throws IOException, URISyntaxException {
        Path dir = getIntegrationDir();
        List<String> names = new ArrayList<>();
        try (
            DirectoryStream<Path> stream = Files.newDirectoryStream(
                dir,
                "*.cfg"
            )
        ) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                String baseName = fileName.substring(
                    0,
                    fileName.length() - ".cfg".length()
                );
                Path expectedOutput = dir.resolve(baseName + ".out");
                assertTrue(
                    Files.exists(expectedOutput),
                    "Missing expected output file: " + expectedOutput
                );
                names.add(baseName);
            }
        }
        names.sort(String::compareTo);
        return names.stream();
    }

    /*
    * For interested students: 
    * This uses a JUnit feature called parameterized tests (which you do not need to know how to use)
    * This allows me to basically run the same test on all the files in the integration directory
    * Without having to write them out here myself!
     */ 
    @ParameterizedTest(name = "{0}")
    @MethodSource("testCases")
    void simulatorProducesExpectedMachineOutput(String testName)
        throws Exception {
        ExpectedActual reports = buildExpectedAndActualReports(testName);
        assertMachinesMatch(reports.expected(), reports.actual(), testName);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testCases")
    void simulatorProducesExpectedBeltOutput(String testName) throws Exception {
        ExpectedActual reports = buildExpectedAndActualReports(testName);
        assertBeltsMatch(reports.expected(), reports.actual(), testName);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testCases")
    void simulatorProducesExpectedSinkOutput(String testName) throws Exception {
        ExpectedActual reports = buildExpectedAndActualReports(testName);
        assertSinkMatch(reports.expected(), reports.actual(), testName);
    }

    // Tests around Belt Validation.

    @Test
    void rejectsMismatchedItemTypesOnSharedBelt() {
        Path cfgFile = getInvalidIntegrationDir().resolve("30_invalid_mixed_belt.cfg");

        assertThrows(BeltValidationException.class, () -> {
            SimulatorConfig config = ConfigParser.parse(cfgFile);
            new Simulator(config);
        });
    }

    @Test
    void rejectsOutputInputItemMismatchOnSharedBelt() {
        Path cfgFile = getInvalidIntegrationDir().resolve(
            "29_invalid_output_input_item_mismatch.cfg"
        );

        assertThrows(BeltValidationException.class, () -> {
            SimulatorConfig config = ConfigParser.parse(cfgFile);
            new Simulator(config);
        });
    }

    @Test
    void rejectsSinkConnectedToMachineInput() {
        Path cfgFile = getInvalidIntegrationDir().resolve("31_invalid_sink_input.cfg");

        assertThrows(BeltValidationException.class, () -> {
            SimulatorConfig config = ConfigParser.parse(cfgFile);
            new Simulator(config);
        });
    }

    @Test
    void checkInitialState() throws Exception {
        SimulatorConfig config = new SimulatorConfig()
            .setWarmupDuration(0)
            .setStatisticsDuration(1)
            .addMachine(
                new factorysim.config.MachineConfig("Ore Mine", 1)
                    .addOutput("ore", 1, "ore_belt")
            )
            .addMachine(
                new factorysim.config.MachineConfig("Ore Consumer", 1)
                    .addInput("ore", 1, "ore_belt")
                    .addOutput("ore", 1, "sink")
            );

        Simulator simulator = new Simulator(config);

        // Deliberately do not call run(); this is simulation time 0 (initial state).
        Report initial = simulator.generateReport();

        assertEquals(2, initial.getMachineReports().size());
        for (MachineStats machine : initial.getMachineReports()) {
            assertEquals(0.0, machine.getUtilisation(), 1e-12);
        }

        assertEquals(1, initial.getBeltReports().size());
        for (BeltStats belt : initial.getBeltReports()) {
            assertEquals(0.0, belt.getItemsPerMinute(), 1e-12);
        }

        assertTrue(initial.getSinkEntries().isEmpty());
    }

    private static ExpectedActual buildExpectedAndActualReports(String testName)
        throws Exception {
        Path dir = getIntegrationDir();
        Path cfgFile = dir.resolve(testName + ".cfg");
        Path outFile = dir.resolve(testName + ".out");

        SimulatorConfig config = ConfigParser.parse(cfgFile);
        Simulator simulator = new Simulator(config);
        simulator.run();
        Report actual = simulator.generateReport();

        String expectedText = Files.readString(outFile);
        Report expected = Report.parse(expectedText);

        return new ExpectedActual(expected, actual);
    }

    private record ExpectedActual(Report expected, Report actual) {}

    // --- Comparison helpers ---

    private static void assertMachinesMatch(
        Report expected,
        Report actual,
        String testName
    ) {
        Map<String, Double> expectedMap = new LinkedHashMap<>();
        for (MachineStats m : expected.getMachineReports()) {
            expectedMap.put(m.getName(), m.getUtilisation() * 100.0);
        }
        Map<String, Double> actualMap = new LinkedHashMap<>();
        for (MachineStats m : actual.getMachineReports()) {
            actualMap.put(m.getName(), m.getUtilisation() * 100.0);
        }

        assertEquals(
            expectedMap.keySet(),
            actualMap.keySet(),
            "Machine name mismatch for test case: " + testName
        );

        for (String name : expectedMap.keySet()) {
            assertEquals(
                expectedMap.get(name),
                actualMap.get(name),
                UTILISATION_TOLERANCE_PCT,
                "Machine utilisation mismatch for '" +
                    name +
                    "' in test case: " +
                    testName
            );
        }
    }

    private static void assertBeltsMatch(
        Report expected,
        Report actual,
        String testName
    ) {
        Map<String, BeltStats> expectedMap = new LinkedHashMap<>();
        for (BeltStats b : expected.getBeltReports()) {
            expectedMap.put(b.getName(), b);
        }
        Map<String, BeltStats> actualMap = new LinkedHashMap<>();
        for (BeltStats b : actual.getBeltReports()) {
            actualMap.put(b.getName(), b);
        }

        assertEquals(
            expectedMap.keySet(),
            actualMap.keySet(),
            "Belt name mismatch for test case: " + testName
        );

        for (String name : expectedMap.keySet()) {
            BeltStats exp = expectedMap.get(name);
            BeltStats act = actualMap.get(name);

            assertEquals(
                exp.getItemType(),
                act.getItemType(),
                "Belt item type mismatch for '" +
                    name +
                    "' in test case: " +
                    testName
            );
            assertEquals(
                exp.getItemsPerMinute(),
                act.getItemsPerMinute(),
                THROUGHPUT_TOLERANCE,
                "Belt throughput mismatch for '" +
                    name +
                    "' in test case: " +
                    testName
            );
        }
    }

    private static void assertSinkMatch(
        Report expected,
        Report actual,
        String testName
    ) {
        Map<String, Double> expectedMap = new LinkedHashMap<>();
        for (SinkEntry s : expected.getSinkEntries()) {
            expectedMap.put(s.getItemType(), s.getItemsPerMinute());
        }
        Map<String, Double> actualMap = new LinkedHashMap<>();
        for (SinkEntry s : actual.getSinkEntries()) {
            actualMap.put(s.getItemType(), s.getItemsPerMinute());
        }

        assertEquals(
            expectedMap.keySet(),
            actualMap.keySet(),
            "Sink item type mismatch for test case: " + testName
        );

        for (String itemType : expectedMap.keySet()) {
            assertEquals(
                expectedMap.get(itemType),
                actualMap.get(itemType),
                THROUGHPUT_TOLERANCE,
                "Sink throughput mismatch for '" +
                    itemType +
                    "' in test case: " +
                    testName
            );
        }
    }

    private static Path getIntegrationDir() {
        return Paths.get(INTEGRATION_DIR);
    }

    private static Path getInvalidIntegrationDir() {
        return Paths.get(INVALID_INTEGRATION_DIR);
    }
}
