package factorysim.model;

/**
 * Represents an input storage port for a machine.
 * An input port stores one item type up to a fixed capacity.
 */
public class InputPort {

    private final String itemType;
    private final int capacity;
    private final String beltName;
    private int itemsStored;

    /**
     * Creates an input port for a machine.
     *
     * @param itemType the type of item this port accepts
     * @param capacity the maximum number of items this port can store
     * @param beltName the belt connected to this input port
     */
    public InputPort(String itemType, int capacity, String beltName) {
        this.itemType = itemType;
        this.capacity = capacity;
        this.beltName = beltName;
        this.itemsStored = 0;
    }

    /**
     * Gets the item type accepted by this input port.
     *
     * @return the item type
     */
    public String itemType() {
        return itemType;
    }

    /**
     * Gets the item type accepted by this input port.
     *
     * @return the item type
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Gets the capacity of this input port.
     *
     * @return the maximum number of items this port can store
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the belt name connected to this input port.
     *
     * @return the connected belt name
     */
    public String getBeltName() {
        return beltName;
    }

    /**
     * Gets the number of items currently stored.
     *
     * @return the current item count
     */
    public int getItemsStored() {
        return itemsStored;
    }

    /**
     * Gets the remaining space in this input port.
     *
     * @return the amount of free storage space
     */
    public int getSpaceRemaining() {
        return capacity - itemsStored;
    }

    /**
     * Checks whether this input port has no stored items.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return itemsStored == 0;
    }

    /**
     * Checks whether this input port is full.
     *
     * @return true if full, false otherwise
     */
    public boolean isFull() {
        return itemsStored >= capacity;
    }

    /**
     * Checks whether another item can be pushed into this input port.
     *
     * @return true if there is free space, false otherwise
     */
    public boolean canPush() {
        return itemsStored < capacity;
    }

    /**
     * Checks whether an item of the given type can be pushed into this port.
     *
     * @param incomingItemType the item type being pushed
     * @return true if the type matches and there is free space
     */
    public boolean canPush(String incomingItemType) {
        return itemType.equals(incomingItemType) && canPush();
    }

    /**
     * Pushes one item into this input port.
     */
    public void pushItem() {
        if (!canPush()) {
            throw new IllegalStateException("Input port is full.");
        }

        itemsStored++;
    }

    /**
     * Pushes one item of the given type into this input port.
     *
     * @param incomingItemType the item type being pushed
     */
    public void pushItem(String incomingItemType) {
        if (!canPush(incomingItemType)) {
            throw new IllegalArgumentException(
                "Cannot push item type " + incomingItemType
                    + " into input port for " + itemType + "."
            );
        }

        pushItem();
    }

    /**
     * Checks whether this input port has enough items for one machine activation.
     *
     * @return true if the port is full, false otherwise
     */
    public boolean hasRequiredItems() {
        return itemsStored >= capacity;
    }

    /**
     * Consumes one full batch of input items for machine activation.
     */
    public void consumeBatch() {
        if (!hasRequiredItems()) {
            throw new IllegalStateException(
                "Not enough items to consume a full input batch."
            );
        }

        itemsStored -= capacity;
    }
}