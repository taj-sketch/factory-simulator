package factorysim.model;

import java.util.List;

import factorysim.stats.BeltStats;
import factorysim.stats.MachineStats;
import factorysim.stats.SinkEntry;

/**
 * An interface that specifies the API for a factory network.
 *
 * The factory should be able to advance by one tick, perform the tock phase,
 * reset its statistics (all inherited from earlier interfaces) 
 * and report its machine, belt, and sink statistics.
 */
public interface FactoryNetwork extends Tickable, Tockable, StatResettable {
    /**
     * Gets a list of SinkEntry objects representing the statistics for the sink component of the factory.
     * @return list of SinkEntry objects
     */
    public List<SinkEntry> getSinkStats();

    /**
     * Gets a list of MachineStats objects representing the statistics for the machine component of the factory.
     * @return list of MachineStats objects
     */
    public List<MachineStats> getMachineStats();

    /**
     * Gets a list of BeltStats objects representing the statistics for the belt component of the factory.
     * @return list of BeltStats objects
     */
    public List<BeltStats> getBeltStats();
}