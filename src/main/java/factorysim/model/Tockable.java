// DO NOT MODIFY THIS FILE

package factorysim.model;

/**
 * An interface for a tockable component of the factory.
 *
 * A tockable component should be able to advance by one tock when the tock() method is called.
 */
public interface Tockable {
    /**
     * Advances the tockable component state by one tock.
     */
    public void tock();
}