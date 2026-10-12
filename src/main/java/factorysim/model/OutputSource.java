package factorysim.model;

/**
 * Minimal readable/pullable output source abstraction used by Sink.
 * You may add to this interface if you think you need to, but 
 * DO NOT CHANGE any of the existing methods, or the Sink tests will fail!
 */
public interface OutputSource {

    /**
     * Gets the type of item that this source provides.
     * Assumes a single source can only provide one type of item.
     * @return a String representing the type of item this source provides.
     */
    String itemType();

    /**
     * Checks if it is possible to pull an item from the source. 
     * @return true if there is at least one item available to pull, and false otherwise.
     */
    boolean canPull();

    /**
     * Pulls a single item from the source. 
     */
    void pullItem();
}