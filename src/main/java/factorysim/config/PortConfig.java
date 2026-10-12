// DO NOT MODIFY THIS FILE
// You will need to understand the get methods in this class to initially construct a factory network
// from a config class. The set methods might be useful for testing.
package factorysim.config;

/**
 * Configuration data for a port (input or output) of a machine, 
 * populated during config file parsing.
 */
public class PortConfig {

    private final String itemName;
    private final int amount;
    private final String beltName;

    /**
     * Creates a new PortConfig with the given item name, amount, and belt name.
     * @param itemName the name of the item type for this port
     * @param amount the capacity of this port (number of items)
     * @param beltName the name of the belt this port connects to.
     */
    public PortConfig(String itemName, int amount, String beltName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException(
                "Item name cannot be null or blank"
            );
        }
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Amount must be positive, got: " + amount
            );
        }
        if (beltName == null || beltName.isBlank()) {
            throw new IllegalArgumentException(
                "Belt name cannot be null or blank"
            );
        }
        this.itemName = itemName;
        this.amount = amount;
        this.beltName = beltName;
    }

    /** 
     * Returns the name of the item type for this port.
     * @return a String representing item type
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Returns the capacity of this port (number of items).
     * @return an integer representing port capacity
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Returns the name of the belt this port connects to.
     * @return a String representing the belt name.
     */
    public String getBeltName() {
        return beltName;
    }
}
