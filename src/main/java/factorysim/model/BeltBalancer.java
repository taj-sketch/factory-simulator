package factorysim.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a belt balancer connecting machine output ports to machine input ports.
 * A belt balancer carries only one item type and moves items using round-robin
 * source and destination selection.
 */
public class BeltBalancer implements Tockable, StatResettable {

    private final String name;
    private String itemType;
    private final List<OutputPort> sources;
    private final List<InputPort> destinations;

    private int nextSourceIndex;
    private int nextDestinationIndex;

    private long transferredItems;
    private long tocksSinceReset;

    /**
     * Creates a belt balancer with the given name.
     *
     * @param name the belt name
     */
    public BeltBalancer(String name) {
        this.name = name;
        this.itemType = null;
        this.sources = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.nextSourceIndex = 0;
        this.nextDestinationIndex = 0;
        this.transferredItems = 0;
        this.tocksSinceReset = 0;
    }

    /**
     * Adds a machine output port as a source for this belt.
     *
     * @param source the output port connected to this belt
     * @throws BeltValidationException if this port has the wrong item type
     */
    public void addSource(OutputPort source) throws BeltValidationException {
        validateItemType(source.getItemType());
        sources.add(source);
    }

    /**
     * Adds a machine input port as a destination for this belt.
     *
     * @param destination the input port connected to this belt
     * @throws BeltValidationException if this port has the wrong item type
     */
    public void addDestination(InputPort destination) throws BeltValidationException {
        validateItemType(destination.getItemType());
        destinations.add(destination);
    }

    /**
     * Checks whether a new port item type is valid for this belt.
     *
     * @param newItemType the item type to validate
     * @throws BeltValidationException if the belt would carry multiple item types
     */
    private void validateItemType(String newItemType) throws BeltValidationException {
        if (itemType == null) {
            itemType = newItemType;
        } else if (!itemType.equals(newItemType)) {
            throw new BeltValidationException(
                "Belt '" + name + "' cannot carry both " + itemType
                    + " and " + newItemType + "."
            );
        }
    }

    /**
     * Gets the belt name.
     *
     * @return the belt name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the item type carried by this belt.
     *
     * @return the item type
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Gets the average number of items transferred per minute.
     *
     * @return the average items per minute
     */
    public double getItemsPerMinute() {
        if (tocksSinceReset == 0) {
            return 0.0;
        }

        return (transferredItems / (double) tocksSinceReset) * 60.0;
    }

    /**
     * Moves items from output ports to input ports.
     * Empty output ports and full input ports are skipped.
     */
    @Override
    public void tock() {
        tocksSinceReset++;

        while (hasPullableSource() && hasPushableDestination()) {
            OutputPort source = nextPullableSource();
            InputPort destination = nextPushableDestination();

            source.pullItem();
            destination.pushItem(source.getItemType());

            transferredItems++;
        }
    }

    /**
     * Checks whether at least one connected source has an item available.
     *
     * @return true if an item can be pulled
     */
    private boolean hasPullableSource() {
        for (OutputPort source : sources) {
            if (source.canPull()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether at least one connected destination has space.
     *
     * @return true if an item can be pushed
     */
    private boolean hasPushableDestination() {
        for (InputPort destination : destinations) {
            if (destination.canPush(itemType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds the next output port that can provide an item.
     *
     * @return the next pullable output port
     */
    private OutputPort nextPullableSource() {
        for (int checked = 0; checked < sources.size(); checked++) {
            int index = (nextSourceIndex + checked) % sources.size();
            OutputPort source = sources.get(index);

            if (source.canPull()) {
                nextSourceIndex = (index + 1) % sources.size();
                return source;
            }
        }

        throw new IllegalStateException("No pullable source found.");
    }

    /**
     * Finds the next input port that can accept an item.
     *
     * @return the next pushable input port
     */
    private InputPort nextPushableDestination() {
        for (int checked = 0; checked < destinations.size(); checked++) {
            int index = (nextDestinationIndex + checked) % destinations.size();
            InputPort destination = destinations.get(index);

            if (destination.canPush(itemType)) {
                nextDestinationIndex = (index + 1) % destinations.size();
                return destination;
            }
        }

        throw new IllegalStateException("No pushable destination found.");
    }

    /**
     * Resets belt statistics while keeping connected ports and round-robin turn order.
     */
    @Override
    public void resetStatistics() {
        transferredItems = 0;
        tocksSinceReset = 0;
    }
}