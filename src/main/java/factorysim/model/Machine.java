package factorysim.model;

import factorysim.config.MachineConfig;
import factorysim.config.PortConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulation model for a factory machine.
 * A machine can consume input batches, produce output batches, enter cooldown,
 * and track utilisation statistics.
 */
public class Machine implements Tickable, StatResettable {

    private final String name;
    private final int cooldown;
    private final List<InputPort> inputPorts;
    private final List<OutputPort> outputPorts;

    private int cooldownRemaining;
    private long ticksSinceReset;
    private long utilisedTicks;

    /**
     * Creates a machine from a MachineConfig object.
     *
     * @param config the machine configuration
     */
    public Machine(MachineConfig config) {
        this.name = config.getName();
        this.cooldown = config.getCooldown();
        this.inputPorts = new ArrayList<>();
        this.outputPorts = new ArrayList<>();

        for (PortConfig inputConfig : config.getInputConfigs()) {
            inputPorts.add(
                new InputPort(
                    inputConfig.getItemName(),
                    inputConfig.getAmount(),
                    inputConfig.getBeltName()
                )
            );
        }

        for (PortConfig outputConfig : config.getOutputConfigs()) {
            outputPorts.add(
                new OutputPort(
                    outputConfig.getItemName(),
                    outputConfig.getAmount(),
                    outputConfig.getBeltName()
                )
            );
        }

        this.cooldownRemaining = 0;
        this.ticksSinceReset = 0;
        this.utilisedTicks = 0;
    }

    /**
     * Gets the machine name.
     *
     * @return the machine name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the machine cooldown.
     *
     * @return the cooldown length in ticks
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Gets the remaining cooldown ticks.
     *
     * @return the remaining cooldown ticks
     */
    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    /**
     * Gets this machine's input ports.
     *
     * @return list of input ports
     */
    public List<InputPort> getInputPorts() {
        return inputPorts;
    }

    /**
     * Gets this machine's output ports.
     *
     * @return list of output ports
     */
    public List<OutputPort> getOutputPorts() {
        return outputPorts;
    }

    /**
     * Checks whether this machine is currently cooling down.
     *
     * @return true if the machine is in cooldown, false otherwise
     */
    public boolean isCoolingDown() {
        return cooldownRemaining > 0;
    }

    /**
     * Checks whether this machine has all required input items.
     * Machines with no inputs are treated as mines/sources and are always
     * considered to have their required inputs.
     *
     * @return true if all input ports are full, false otherwise
     */
    public boolean hasRequiredInputs() {
        for (InputPort inputPort : inputPorts) {
            if (!inputPort.hasRequiredItems()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether all output ports are empty.
     * A machine may only activate when all its output ports are empty.
     *
     * @return true if all output ports are empty, false otherwise
     */
    public boolean hasEmptyOutputs() {
        for (OutputPort outputPort : outputPorts) {
            if (!outputPort.canProduceBatch()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether this machine can activate on the current tick.
     *
     * @return true if the machine can activate, false otherwise
     */
    public boolean canActivate() {
        return !isCoolingDown() && hasRequiredInputs() && hasEmptyOutputs();
    }

    /**
     * Advances this machine by one tick.
     * If cooling down, the cooldown counter decreases.
     * If ready, the machine activates by consuming input batches and producing
     * output batches.
     */
    @Override
    public void tick() {
        ticksSinceReset++;

        if (isCoolingDown()) {
            cooldownRemaining--;
            utilisedTicks++;
            return;
        }

        if (canActivate()) {
            activate();
            utilisedTicks++;
        }
    }

    /**
     * Activates this machine once.
     * This consumes all required input batches, fills all output ports with
     * one output batch, and starts cooldown.
     */
    private void activate() {
        for (InputPort inputPort : inputPorts) {
            inputPort.consumeBatch();
        }

        for (OutputPort outputPort : outputPorts) {
            outputPort.produceBatch();
        }

        cooldownRemaining = cooldown;
    }

    /**
     * Gets this machine's utilisation since the last statistics reset.
     *
     * @return utilisation as a value from 0.0 to 1.0
     */
    public double getUtilisation() {
        if (ticksSinceReset == 0) {
            return 0.0;
        }

        return utilisedTicks / (double) ticksSinceReset;
    }

    /**
     * Gets the number of ticks counted since the last statistics reset.
     *
     * @return the number of ticks since reset
     */
    public long getTicksSinceReset() {
        return ticksSinceReset;
    }

    /**
     * Gets the number of utilised ticks since the last statistics reset.
     *
     * @return the number of utilised ticks
     */
    public long getUtilisedTicks() {
        return utilisedTicks;
    }

    /**
     * Resets only statistics fields.
     * The physical machine state, including stored input/output items and
     * remaining cooldown, is preserved.
     */
    @Override
    public void resetStatistics() {
        ticksSinceReset = 0;
        utilisedTicks = 0;
    }
}