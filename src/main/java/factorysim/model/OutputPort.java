package factorysim.model;

/**
 * Represents an output storage port for a machine.
 * An output port stores one item type and allows belts or the sink to pull
 * items from it.
 */
public class OutputPort implements OutputSource {

    private final String itemType;
    private final int capacity;
    private final String beltName;
    private int itemsStored;

    /**
     * Creates an output port for a machine.
     *
     * @param itemType the type of item this output port produces
     * @param capacity the number of items produced in one machine activation
     * @param beltName the belt connected to this output port
     */
    public OutputPort(String itemType, int capacity, String beltName) {
        this.itemType = itemType;
        this.capacity = capacity;
        this.beltName = beltName;
        this.itemsStored = 0;
    }

    /**
     * Gets the item type provided by this output port.
     *
     * @return the item type
     */
    @Override
    public String itemType() {
        return itemType;
    }

    /**
     * Gets the item type provided by this output port.
     *
     * @return the item type
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Gets the output port capacity.
     *
     * @return the maximum number of items this output port can store
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the belt name connected to this output port.
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
     * Checks whether this output port is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return itemsStored == 0;
    }

    /**
     * Checks whether this output port is full.
     *
     * @return true if full, false otherwise
     */
    public boolean isFull() {
        return itemsStored >= capacity;
    }

    /**
     * Checks whether this output port has at least one item available.
     *
     * @return true if an item can be pulled, false otherwise
     */
    @Override
    public boolean canPull() {
        return itemsStored > 0;
    }

    /**
     * Pulls one item from this output port.
     */
    @Override
    public void pullItem() {
        if (!canPull()) {
            throw new IllegalStateException("Output port is empty.");
        }

        itemsStored--;
    }

    /**
     * Checks whether this port can receive a full output batch from its machine.
     * A machine may only activate when all its output ports are empty.
     *
     * @return true if this port is empty, false otherwise
     */
    public boolean canProduceBatch() {
        return isEmpty();
    }

    /**
     * Stores one full output batch in this port.
     */
    public void produceBatch() {
        if (!canProduceBatch()) {
            throw new IllegalStateException(
                "Cannot produce into a non-empty output port."
            );
        }

        itemsStored = capacity;
    }
}