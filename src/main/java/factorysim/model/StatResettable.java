// DO NOT MODIFY THIS FILE

package factorysim.model;

/**
 * An interface for a component that can reset any fields used to calculate its statistics to their
 * initial state.
 *
 * A component's state should otherwise still be preserved. E.g. If a machine is only part-way through its cooldown
 * period, it should still be in cool-down mode after resetStatistics() is called.
 *
 * e.g. The Simulator calls resetStatistics() on the FactoryNetwork between the warm-up phase
 * and main statistics collection phase of the simulation run, so that the statistics reported at the end
 * of the simulation run only reflect the main statistics collection phase and not the warm-up phase.
 */
public interface StatResettable {

    /**
     * Resets the statistics of the component to their initial state.
     * The exact meaning of "resetting" depends on the component.
     *
     * For example, it should reset any private fields used to calculate the statistics.
     *
     * E.g. this could include a field counting the number of tocks that have occurred for a component.
     */
    public void resetStatistics();
}