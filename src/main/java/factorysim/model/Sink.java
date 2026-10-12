package factorysim.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulation model for a sink. A sink is a special consumer that accepts
 * unlimited items from connected output sources and records statistics about
 * consumed items.
 */
public class Sink implements Tockable, StatResettable {

    /** The reserved belt name for the sink. */
    public static final String BELT_NAME = "sink";

    private final List<OutputSource> sources;
    private final Map<String, Long> itemsConsumed;
    private long tocksSinceReset;

    /**
     * Creates an empty sink with no connected output sources.
     */
    public Sink() {
        sources = new ArrayList<>();
        itemsConsumed = new LinkedHashMap<>();
        tocksSinceReset = 0;
    }

    /**
     * Adds an output source for this sink to consume from.
     *
     * @param source the output source connected to the sink
     */
    public void addSource(OutputSource source) {
        sources.add(source);
    }

    /**
     * Consumes all available items from every connected output source.
     */
    @Override
    public void tock() {
        tocksSinceReset++;

        for (OutputSource source : sources) {
            while (source.canPull()) {
                String itemType = source.itemType();
                source.pullItem();

                long currentCount = getItemsConsumed(itemType);
                itemsConsumed.put(itemType, currentCount + 1);
            }
        }
    }

    /**
     * Gets the item types consumed by this sink since the last statistics reset.
     *
     * @return a list of consumed item types
     */
    public List<String> getItemTypes() {
        return new ArrayList<>(itemsConsumed.keySet());
    }

    /**
     * Gets the average number of items consumed per minute for a given item type.
     *
     * @param itemType the item type to check
     * @return the average items consumed per minute
     */
    public double getAvgItemsPerMinute(String itemType) {
        if (tocksSinceReset == 0) {
            return 0.0;
        }

        return (getItemsConsumed(itemType) / (double) tocksSinceReset) * 60.0;
    }

    /**
     * Gets the number of items consumed for a given item type.
     *
     * @param itemType the item type to check
     * @return the number of items consumed
     */
    public long getItemsConsumed(String itemType) {
        return itemsConsumed.getOrDefault(itemType, 0L);
    }

    /**
     * Checks whether this sink has consumed any items since the last reset.
     *
     * @return true if no items have been consumed, false otherwise
     */
    public boolean isEmpty() {
        return itemsConsumed.isEmpty();
    }

    /**
     * Clears all sink statistics while keeping connected sources.
     */
    @Override
    public void resetStatistics() {
        itemsConsumed.clear();
        tocksSinceReset = 0;
    }
}
