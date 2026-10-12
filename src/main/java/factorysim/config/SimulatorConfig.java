// DO NOT MODIFY THIS FILE
// You will need to understand the get methods in this class to initially construct a factory network
// from a config class. 
package factorysim.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration data for the entire simulation, populated during config file parsing.
 * Uses a builder-like pattern where setters return {@code this} for method chaining (see MachineConfig for example). 
 */
public class SimulatorConfig {

    private int warmupDuration = -1; // -1 indicates not yet set
    private int statisticsDuration = -1; // -1 indicates not yet set
    private final List<MachineConfig> machines = new ArrayList<MachineConfig>();

    /**
     * Sets the warmup duration for the simulation.
     * @param warmupDuration duration of the warmup phase in number of steps
     * @return this SimulatorConfig for object building
     * @throws IllegalArgumentException if warmupDuration is negative
     */
    public SimulatorConfig setWarmupDuration(int warmupDuration) {
        if (warmupDuration < 0) {
            throw new IllegalArgumentException(
                "Warmup duration cannot be negative, got: " + warmupDuration
            );
        }
        this.warmupDuration = warmupDuration;
        return this;
    }

    /**
     * Sets the statistics collection duration for the simulation.
     * @param statisticsDuration duration of the statistics collection phase in number of steps
     * @return this SimulatorConfig for object building.
     * @throws IllegalArgumentException if statisticsDuration is not positive
     */
    public SimulatorConfig setStatisticsDuration(int statisticsDuration) {
        if (statisticsDuration <= 0) {
            throw new IllegalArgumentException(
                "Statistics duration must be positive, got: " +
                    statisticsDuration
            );
        }
        this.statisticsDuration = statisticsDuration;
        return this;
    }

    /**
     *  Adds a machine configuration to the simulation configuration.
     *  @param machine must be complete (see {@link MachineConfig#isComplete()})
     * @throws IllegalArgumentException if machine is null or incomplete
     */
    public SimulatorConfig addMachine(MachineConfig machine) {
        if (machine == null) {
            throw new IllegalArgumentException("Machine config cannot be null");
        }
        if (!machine.isComplete()) {
            throw new IllegalArgumentException(
                "Machine config is incomplete: " + machine
            );
        }
        machines.add(machine);
        return this;
    }

    /**
     * Returns the duration of the warmup phase
     * @return an integer representing the warmup duration in steps (seconds).
     */
    public int getWarmupDuration() {
        return warmupDuration;
    }

    /**
     * Returns the duration of the statistics collection phase.
     * @return an integer representing the statistics duration in steps (seconds).
     */
    public int getStatisticsDuration() {
        return statisticsDuration;
    }

    /**
     * Returns a copy of the list of machine configurations in the order they 
     * appeared in the config file.
     * @return a list of machine configurations.
     */
    public List<MachineConfig> getMachineConfigs() {
        return List.copyOf(machines);
    }

    /**
     * @return true if warmup and statistics durations are set
     */
    public boolean isComplete() {
        return warmupDuration >= 0 && statisticsDuration > 0;
    }
}
