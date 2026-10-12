// DO NOT MODIFY THIS FILE
package factorysim.stats;

/**
 * An immutable record of statistics for a sink entry in the factory.
 * 
 * If a sink consumes multiple types of items, it will need multiple Sink Entries (one per item).
 * 
 * Each SinkEntry object contains the type of an item consumed by the sink
 * and the number of items of that type consumed per minute by the sink.
 */
public final class SinkEntry {
    private final String itemType;
    private final double itemsPerMinute;

    public SinkEntry(String itemType, double itemsPerMinute) {
        this.itemType = itemType;
        this.itemsPerMinute = itemsPerMinute;
    }

    /**
     * Gets the type of item consumed by the sink entry.
     * @return item type
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Gets the number of items per minute consumed by the sink entry at the current point
     * in the simulation run.
     * @return items per minute (may be a decimal value)
     */
    public double getItemsPerMinute() {
        return itemsPerMinute;
    }
}