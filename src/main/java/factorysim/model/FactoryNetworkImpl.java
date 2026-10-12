package factorysim.model;

import factorysim.config.MachineConfig;
import factorysim.stats.BeltStats;
import factorysim.stats.MachineStats;
import factorysim.stats.SinkEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Models the full factory network by wiring machines, belts, and the sink
 * together from machine configuration data.
 */
public final class FactoryNetworkImpl implements FactoryNetwork {

    private final Sink sink;
    private final List<Machine> machines;
    private final Map<String, BeltBalancer> belts;

    /**
     * Establishes a factory network from machine configurations.
     *
     * @param machineConfigs the machine configurations in config file order
     * @throws BeltValidationException if a belt connection is invalid
     */
    public FactoryNetworkImpl(List<MachineConfig> machineConfigs)
        throws BeltValidationException {

        sink = new Sink();
        machines = new ArrayList<>();
        belts = new LinkedHashMap<>();

        for (MachineConfig machineConfig : machineConfigs) {
            machines.add(new Machine(machineConfig));
        }

        wireOutputs();
        wireInputs();
    }

    /**
     * Connects every machine output to either the sink or a normal belt.
     *
     * @throws BeltValidationException if the output cannot be connected
     */
    private void wireOutputs() throws BeltValidationException {
        for (Machine machine : machines) {
            for (OutputPort outputPort : machine.getOutputPorts()) {
                String beltName = outputPort.getBeltName();

                if (Sink.BELT_NAME.equals(beltName)) {
                    sink.addSource(outputPort);
                } else {
                    getOrCreateBelt(beltName).addSource(outputPort);
                }
            }
        }
    }

    /**
     * Connects every machine input to a normal belt.
     * The reserved sink belt may not be connected to a machine input.
     *
     * @throws BeltValidationException if the input cannot be connected
     */
    private void wireInputs() throws BeltValidationException {
        for (Machine machine : machines) {
            for (InputPort inputPort : machine.getInputPorts()) {
                String beltName = inputPort.getBeltName();

                if (Sink.BELT_NAME.equals(beltName)) {
                    throw new BeltValidationException(
                        "The sink belt cannot be connected to an input port."
                    );
                }

                getOrCreateBelt(beltName).addDestination(inputPort);
            }
        }
    }

    /**
     * Gets an existing belt, or creates one if this is its first connection.
     *
     * @param beltName the belt name
     * @return the matching belt balancer
     */
    private BeltBalancer getOrCreateBelt(String beltName) {
        if (!belts.containsKey(beltName)) {
            belts.put(beltName, new BeltBalancer(beltName));
        }

        return belts.get(beltName);
    }

    /**
     * Advances all machines by one tick.
     */
    @Override
    public void tick() {
        for (Machine machine : machines) {
            machine.tick();
        }
    }

    /**
     * Advances all belts and the sink by one tock.
     */
    @Override
    public void tock() {
        for (BeltBalancer belt : belts.values()) {
            belt.tock();
        }

        sink.tock();
    }

    /**
     * Resets statistics for all factory components while preserving state.
     */
    @Override
    public void resetStatistics() {
        for (Machine machine : machines) {
            machine.resetStatistics();
        }

        for (BeltBalancer belt : belts.values()) {
            belt.resetStatistics();
        }

        sink.resetStatistics();
    }

    /**
     * Gets statistics for all items consumed by the sink.
     *
     * @return sink statistics entries
     */
    @Override
    public List<SinkEntry> getSinkStats() {
        List<SinkEntry> sinkStats = new ArrayList<>();

        for (String itemType : sink.getItemTypes()) {
            sinkStats.add(
                new SinkEntry(itemType, sink.getAvgItemsPerMinute(itemType))
            );
        }

        return sinkStats;
    }

    /**
     * Gets utilisation statistics for all machines.
     *
     * @return machine statistics entries
     */
    @Override
    public List<MachineStats> getMachineStats() {
        List<MachineStats> machineStats = new ArrayList<>();

        for (Machine machine : machines) {
            machineStats.add(
                new MachineStats(machine.getName(), machine.getUtilisation())
            );
        }

        return machineStats;
    }

    /**
     * Gets throughput statistics for all normal belts.
     *
     * @return belt statistics entries
     */
    @Override
    public List<BeltStats> getBeltStats() {
        List<BeltStats> beltStats = new ArrayList<>();

        for (BeltBalancer belt : belts.values()) {
            beltStats.add(
                new BeltStats(
                    belt.getName(),
                    belt.getItemType(),
                    belt.getItemsPerMinute()
                )
            );
        }

        return beltStats;
    }
}