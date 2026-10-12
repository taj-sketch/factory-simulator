// DO NOT MODIFY THIS FILE
// You will need to understand the get methods in this class to initially construct a factory network
// from a config class. The set/add methods might be useful for testing.
package factorysim.config;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration data for a single machine, populated during config file parsing.
 * Uses a builder-like pattern where setters return {@code this} for method chaining.
 * 
 * e.g. A Machine could be created like this:
 * MachineConfig(name, cooldown)
            .addInput(inputItem, inputAmount, "in_belt")
            .addOutput(outputItem, outputAmount, "out_belt");
 * (More .addInput and .outOutput method calls could continue to be chained.)
 */
public class MachineConfig {

    private String name;
    private final List<PortConfig> inputs = new ArrayList<>();
    private final List<PortConfig> outputs = new ArrayList<>();
    private int cooldown = -1; // -1 indicates not yet set

    public MachineConfig() {
        // empty constructor for builder pattern
    }

    /** 
     * Constructor when name/cooldown are known 
     * Useful for testing.
     */
    public MachineConfig(String name, int cooldown) {
        this.setName(name);
        this.setCooldown(cooldown);
    }

    // default setter as only used in package (e.g. by config tests)
    MachineConfig setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        this.name = name;
        return this;
    }

    /**
     * Adds an input port to the machine.
     * @param portConfig the input port configuration (if already created)
     * @return this MachineConfig instance for config building
     */
    public MachineConfig addInput(PortConfig portConfig) {
        inputs.add(portConfig);
        return this;
    }

    /**
     * Adds an input port to the machine.
     * @param itemName The name of the input port
     * @param amount the capacity of the port
     * @param beltName the name of the belt
     * @return this MachineConfig instance for config building
     */
    public MachineConfig addInput(String itemName, int amount, String beltName) {
        return addInput(new PortConfig(itemName, amount, beltName));
    }

    /**
     * Adds an output port to the machine.
     * @param portConfig the output port configuration (if already created)
     * @return this MachineConfig instance for config building
     */
    public MachineConfig addOutput(PortConfig portConfig) {
        outputs.add(portConfig);
        return this;
    }

    /**
     * Adds an output port to the machine.
     * @param itemName The name of the output port
     * @param amount the capacity of the port
     * @param beltName the name of the belt
     * @return this MachineConfig instance for config building
     */
    public MachineConfig addOutput(
        String itemName,
        int amount,
        String beltName
    ) {
        return addOutput(new PortConfig(itemName, amount, beltName));
    }

    /**
     * Sets the cooldown period for the machine.
     * @param cooldown the number of seconds between activations (must be positive)
     */
    protected MachineConfig setCooldown(int cooldown) {
        if (cooldown < 0) {
            throw new IllegalArgumentException(
                "Cooldown must be positive, got: " + cooldown
            );
        }
        this.cooldown = cooldown;
        return this;
    }

    /**
     * Gets the name of the machine.
     * @return the machine name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the input port configurations for the machine.
     * @return a list of PortConfig objects representing the machine's input ports
     */
    public List<PortConfig> getInputConfigs() {
        return List.copyOf(inputs);
    }

    /**
     * Gets the output port configurations for the machine.
     * @return a list of PortConfig objects representing the machine's output ports. 
     */
    public List<PortConfig> getOutputConfigs() {
        return List.copyOf(outputs);
    }

    /**
     * Gets the cooldown period for the machine.
     * @return the cooldown period in steps (seconds) between activations
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * @return true if name and cooldown are set (inputs/outputs may be empty)
     */
    public boolean isComplete() {
        return name != null && !name.isBlank() && cooldown >= 0;
    }

    /**
     * A very basic override of toString for easier debugging and test failure messages.
     */
    @Override
    public String toString() {
        return name;
    }
}
