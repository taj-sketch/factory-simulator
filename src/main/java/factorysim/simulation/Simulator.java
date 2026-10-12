// DO NOT MODIFY THIS FILE
// You do not need to use this file to understand the assignment, but you may find it useful to read through 
// it to understand how the simulation works.
package factorysim.simulation;

import factorysim.config.SimulatorConfig;
import factorysim.model.BeltValidationException;
import factorysim.model.FactoryNetwork;
import factorysim.model.FactoryNetworkImpl;

/**
 * Orchestrates the simulation of a factory.
 *
 * Constructed from a {@link SimulatorConfig}, the simulator creates a FactoryNetwork
 * model, then runs the tick/tock
 * simulation loop for warmup followed by statistics collection.
 */
public final class Simulator {

    private final int warmupDuration;
    private final int statisticsDuration;
    private FactoryNetwork factoryNetwork;

    /**
     * Creates a new simulator to test a factory design.
     * @param config the simulation configuration
     */
    public Simulator(SimulatorConfig config) throws BeltValidationException{
        this.warmupDuration = config.getWarmupDuration();
        this.statisticsDuration = config.getStatisticsDuration();
        this.factoryNetwork = new FactoryNetworkImpl(config.getMachineConfigs());
    }

    /**
     * Runs the full simulation: warmup phase, then statistics collection phase.
     */
    public void run() {
        for (int t = 0; t < warmupDuration; t++) {
            step();
        }

        factoryNetwork.resetStatistics();

        for (int t = 0; t < statisticsDuration; t++) {
            step();
        }
    }

    /**
     * Performs a single simulation step (one tick + one tock).
     * This represents a second in the real-time simulation.
     */
    public void step() {
        factoryNetwork.tick();
        factoryNetwork.tock();
    }

    /** Generates an immutable {@link Report} snapshot from current state of factoryNetwork. */
    public Report generateReport() {
        return new Report(factoryNetwork.getMachineStats(), factoryNetwork.getBeltStats(), 
            factoryNetwork.getSinkStats());
    }
}
