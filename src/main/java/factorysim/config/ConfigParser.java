// DO NOT MODIFY THIS FILE
// YOU DO NOT NEED TO USE OR UNDERSTAND THIS CLASS TO COMPLETE THE ASSIGNMENT.
package factorysim.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

/**
 * Parser for the factorysim configuration file format.
 */
public final class ConfigParser {

    private final BufferedReader reader;
    private final SimulatorConfig config;
    private int lineNumber;
    private String nextLine; // null if EOF

    /**
     * Creates a new ConfigParser that will read from the given reader.
     * @param reader the buffer
     */
    private ConfigParser(BufferedReader reader) {
        this.reader = reader;
        this.config = new SimulatorConfig();
    }

    /**
     * Loads the next non-blank, non-comment line into nextLine.
     * Lines are stripped of comments (everything from '#' onward) before
     * checking whether they are blank.
     * @return the next line, or null if EOF is reached
     * @throws IOException if an I/O error occurs
     */
    private String pollLine() throws IOException {
        do {
            lineNumber++;
            nextLine = reader.readLine();
            if (nextLine != null) {
                int commentIndex = nextLine.indexOf('#');
                if (commentIndex >= 0) {
                    nextLine = nextLine.substring(0, commentIndex);
                }
            }
        } while (nextLine != null && nextLine.isBlank());
        return nextLine;
    }

    /**
     * Parses a configuration from the specified file path.
     * @param path the path to the configuration file
     * @return a simulator configuration object representing the factory described in the file
     * @throws IOException if an I/O error occurs
     * @throws ConfigParseException if the configuration file is malformed
     */
    public static SimulatorConfig parse(Path path)
        throws IOException, ConfigParseException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return parse(reader);
        }
    }

    /**
     * Parses a configuration from the given reader.
     * @param reader the reader to parse from
     * @return a simulator configuration object representing the factory described 
     * by the text read from the reader
     * @throws IOException if an I/O error occurs
     * @throws ConfigParseException if the configuration is malformed.
     */
    public static SimulatorConfig parse(Reader reader)
        throws IOException, ConfigParseException {
        BufferedReader buffered =
            reader instanceof BufferedReader
                ? (BufferedReader) reader
                : new BufferedReader(reader);
        return new ConfigParser(buffered).parse();
    }

    // private method to parse a configuration that does the actual parsing work, 
    // assuming the reader is already buffered.
    private SimulatorConfig parse() throws IOException, ConfigParseException {
        pollLine(); // Load first line

        while (parseSection()) {
            // Continue parsing sections
        }

        return config;
    }

    // private method to parse a single section of a configuration 
    private boolean parseSection() throws IOException, ConfigParseException {
        if (nextLine == null) {
            return false; // EOF
        }

        String sectionName = tryParseSectionHeader();
        switch (sectionName) {
            case null:
                throw new ConfigParseException(
                    "Expected section header, found: " + nextLine,
                    lineNumber
                );
            case "config":
                parseConfigSection();
                break;
            case "machine":
                parseMachineSection();
                break;
            default:
                throw new ConfigParseException(
                    "Unknown section type: " + sectionName,
                    lineNumber
                );
        }

        return true;
    }

    // private method to parse a section header. 
    // If the next line is a section header, returns the section name.
    private String tryParseSectionHeader() {
        if (!isSectionHeader(nextLine)) {
            return null;
        }

        return nextLine
            .substring(1, nextLine.length() - 1)
            .trim()
            .toLowerCase(Locale.ENGLISH);
    }

    // private method to test if a line is a section header.
    private boolean isSectionHeader(String line) {
        return line.startsWith("[") && line.endsWith("]");
    }

    // private method to parse a key-value pair line, e.g. "name: Ore Mine".
    private Map.Entry<String, String> tryParseKeyValue() {
        if (nextLine == null) {
            return null;
        }
        int colonIndex = nextLine.indexOf(':');
        if (isSectionHeader(nextLine) || colonIndex == -1) {
            return null;
        }
        String key = nextLine
            .substring(0, colonIndex)
            .trim()
            .toLowerCase(Locale.ENGLISH);
        String value = nextLine.substring(colonIndex + 1).trim();

        return Map.entry(key, value);
    }

    // private method to parse a config section (minus the header)
    private void parseConfigSection() throws IOException, ConfigParseException {
        int sectionStartLine = lineNumber;

        pollLine(); // Consume section header

        Map.Entry<String, String> kv;
        while ((kv = tryParseKeyValue()) != null) {
            try {
                switch (kv.getKey()) {
                    case "warmup_duration":
                        config.setWarmupDuration(parseInt(kv.getValue()));
                        break;
                    case "statistics_duration":
                        config.setStatisticsDuration(parseInt(kv.getValue()));
                        break;
                    default:
                        throw new ConfigParseException(
                            "Unknown config option: " + kv.getKey(),
                            lineNumber
                        );
                }
            } catch (IllegalArgumentException e) {
                throw new ConfigParseException(
                    "Invalid config section",
                    sectionStartLine,
                    e
                );
            }
            pollLine(); // Consume parsed line
        }
    }

    // private method to parse a machine section. 
    private void parseMachineSection()
        throws IOException, ConfigParseException {
        MachineConfig machine = new MachineConfig();
        int sectionStartLine = lineNumber;

        pollLine(); // Consume section header

        Map.Entry<String, String> kv;
        while ((kv = tryParseKeyValue()) != null) {
            try {
                switch (kv.getKey()) {
                    case "name":
                        machine.setName(kv.getValue());
                        break;
                    case "cooldown":
                        machine.setCooldown(parseInt(kv.getValue()));
                        break;
                    case "input":
                        machine.addInput(parsePortConfig(kv.getValue()));
                        break;
                    case "output":
                        machine.addOutput(parsePortConfig(kv.getValue()));
                        break;
                    default:
                        throw new ConfigParseException(
                            "Unknown machine option: " + kv.getKey(),
                            lineNumber
                        );
                }
            } catch (IllegalArgumentException e) {
                throw new ConfigParseException(
                    "Invalid machine section",
                    sectionStartLine,
                    e
                );
            }
            pollLine(); // Consume parsed line
        }

        try {
            config.addMachine(machine);
        } catch (IllegalArgumentException e) {
            throw new ConfigParseException(
                "Invalid machine",
                sectionStartLine,
                e
            );
        }
    }

    // private method to parse a port config section
    private PortConfig parsePortConfig(String value) throws ConfigParseException {
        String[] parts = value.split(",");
        if (parts.length != 3) {
            throw new ConfigParseException(
                "Expected 'item, amount, belt_name', found: " + value,
                lineNumber
            );
        }

        String itemName = parts[0].trim();
        int amount = parseInt(parts[1].trim());
        String beltName = parts[2].trim();

        return new PortConfig(itemName, amount, beltName);
    }

    // private method to parse an integer from a string.
    private int parseInt(String value) throws ConfigParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigParseException(
                "Expected integer, found: " + value,
                lineNumber,
                e
            );
        }
    }
}
