// DO NOT MODIFY THIS FILE
package factorysim.stats;

/**
 * An immutable record of statistics for a machine in the factory.
 * 
 * Each MachineStats object contains the name of the machine and
 * a fraction (0.0 to 1.0) of the number of ticks spent being utilised
 * (i.e. either in cool-down mode or an activation tick) vs 
 * the total number of ticks the factory model has advanced through.
 */
public final class MachineStats {
    private final String name;
    private final double utilisation;

    /**
     * Creates a new MachineStats with the given name and utilisation.
     * @param name String representing machine name
     * @param utilisation a double between 0 and 1 representing the fraction of ticks
     * the machine was utilised. Your double does not need to be rounded 
     * (the Report class manages rounding for display).
     */
    public MachineStats(String name, double utilisation) {
        this.name = name;
        this.utilisation = utilisation;
    }

    /**
     * Gets the name of the machine.
     * @return String representing machine name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the utilisation of the machine as a fraction between 0 and 1.
     * @return utilisation double.
     */
    public double getUtilisation() {
        return utilisation;
    }
}