// DO NOT MODIFY THIS FILE
package factorysim.stats;

/**
 * An immutable record of statistics for a belt in the factory.
 * 
 * Each BeltStats object contains the name of the belt, the type of item it carries, 
 * and the number of items per minute processed by the belt based on the current
 * number of tocks the factory model has advanced through.
 */
public final class BeltStats {
    private final String name;
    private final String itemType;
    private final double itemsPerMinute;

    public BeltStats(String name, String itemType, double itemsPerMinute) {
        this.name = name;
        this.itemType = itemType;
        this.itemsPerMinute = itemsPerMinute;
    }

    /**
     * Gets the name of the belt.
     * @return belt name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of item carried by the belt.
     * @return string representing the item type.
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Gets the number of items per minute processed by the belt.
     * @return items per minute (may be a decimal value)
     */
    public double getItemsPerMinute() {
        return itemsPerMinute;
    }
}