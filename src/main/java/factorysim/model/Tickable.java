// DO NOT MODIFY THIS FILE

package factorysim.model;

/**
 * An interface for a tickable component of the factory.
 *
 * A tickable component should be able to advance by one tick when the tick() method is called.
 */
public interface Tickable {

    /**
     * Advances the tickable component state by one tick.
     */
    public void tick();
}