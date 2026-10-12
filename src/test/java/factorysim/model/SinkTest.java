// DO NOT MODIFY THIS FILE

package factorysim.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Sink class.
 */
public class SinkTest {

    /**
     * Tests that a fresh Sink starts empty and has no item types.
     */
    @Test
    void freshSinkStartsEmpty() {
        Sink sink = new Sink();

        assertTrue(sink.isEmpty());
        assertTrue(sink.getItemTypes().isEmpty());
    }

    /**
     * Tests that requesting statistics for an unknown item type returns zero values.
     */
    @Test
    void unknownItemTypeHasZeroItemsConsumed() {
        Sink sink = new Sink();

        assertEquals(0L, sink.getItemsConsumed("Ore"));
    }

    /**
     * Tests that requesting average items per minute for an unknown item type returns zero.
     */
    @Test
    void unknownItemTypeHasZeroItemsPerMinute() {
        Sink sink = new Sink();

        assertEquals(0.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
    }

    /**
     * Tests that the sink correctly pulls items from its sources and updates statistics 
     * accordingly.
     */
    @Test
    void pullsAllItemsFromSources() {
        FakeOutputSource output = new FakeOutputSource("Ore", 5);

        Sink sink = new Sink();
        sink.addSource(output);

        sink.tock();

        assertFalse(output.canPull()); // Has pulled everything in tock.
        assertEquals(5L, sink.getItemsConsumed("Ore")); // Should be recorded.
    }

    /**
     * Tests that the sink correctly tracks multiple item types and their statistics.
     */
    @Test
    void tracksMultipleItemTypes() {
        FakeOutputSource oreSource = new FakeOutputSource("Ore", 3);
        FakeOutputSource coalSource = new FakeOutputSource("Coal", 2);

        Sink sink = new Sink();
        sink.addSource(oreSource);
        sink.addSource(coalSource);

        sink.tock();

        assertEquals(3L, sink.getItemsConsumed("Ore"));
        assertEquals(2L, sink.getItemsConsumed("Coal"));
    }

    /**
     * Tests that getItemTypes()  returns all consumed item types.
     */
    @Test
    void getItemTypesReturnsConsumedTypes() {
        FakeOutputSource oreSource = new FakeOutputSource("Ore", 1);
        FakeOutputSource coalSource = new FakeOutputSource("Coal", 1);

        Sink sink = new Sink();
        sink.addSource(oreSource);
        sink.addSource(coalSource);

        sink.tock();

        assertEquals(2, sink.getItemTypes().size());
        assertTrue(sink.getItemTypes().contains("Ore"));
        assertTrue(sink.getItemTypes().contains("Coal"));
    }

    /**
     * Tests that getItemTypes() only includes sources it has consumed something from.
     */
    @Test
    void getItemTypesReturnsConsumedTypesEmptySource() {
        FakeOutputSource oreSource = new FakeOutputSource("Ore", 1);
        FakeOutputSource coalSource = new FakeOutputSource("Coal", 0);

        Sink sink = new Sink();
        sink.addSource(oreSource);
        sink.addSource(coalSource);

        sink.tock();

        assertEquals(1, sink.getItemTypes().size());
        assertTrue(sink.getItemTypes().contains("Ore"));
    }

    /**
     * Tests that the sink accumulates statistics across multiple tocks and does not reset
     */
    @Test
    void accumulatesAcrossMultipleTocks() {
        FakeOutputSource source = new FakeOutputSource("Ore", 10);

        Sink sink = new Sink();
        sink.addSource(source);

        int cycles = 5;
        for (int i = 0; i < cycles; i++) {
            sink.tock();
            source.storedAmount = 10; // Refill the source each cycle
        }

        assertEquals(50L, sink.getItemsConsumed("Ore"));
    }

    /**
     * Tests that the sink accumulates statistics across multiple tocks
     * with varied number of items to pull.
     */
    @Test
    void accumulatesAcrossMultipleTocksVaryingAmounts() {
        FakeOutputSource source = new FakeOutputSource("Ore", 10);

        Sink sink = new Sink();
        sink.addSource(source);

        int cycles = 5;
        for (int i = 0; i < cycles; i++) {
            sink.tock();
            source.storedAmount = (i+1)*10; // Refill the source each cycle
        }
        // 10 + 10 + 20 + 30 + 40 = 110 items
        assertEquals(110L, sink.getItemsConsumed("Ore"));
    }

    /**
     * Tests that itemsConsumed works correctly across multiple tocks.
     * and only counts a single item type.
     */
    @Test
    void itemsConsumedAcrossMultipleTocksMultipleSources() {
        FakeOutputSource source = new FakeOutputSource("Ore", 10);
        FakeOutputSource coalSource = new FakeOutputSource("Coal", 20);

        Sink sink = new Sink();
        sink.addSource(source);
        sink.addSource(coalSource);

        int cycles = 5;
        for (int i = 0; i < cycles; i++) {
            sink.tock();
            source.storedAmount = 10; // Refill the source each cycle
            coalSource.storedAmount = 20; // Refill the coal source each cycle
        }

        // 50 items in 5 ticks
        assertEquals(50L, sink.getItemsConsumed("Ore"));
        // 100 items in 5 ticks
        assertEquals(100L, sink.getItemsConsumed("Coal"));
    }

    /**
     * Tests that the average items per minute calculation is correct based on the number
     * of items consumed and the number of tocks.
     */
    @Test
    void itemsPerMinuteCalculation() {
        FakeOutputSource source = new FakeOutputSource("Ore", 10);

        Sink sink = new Sink();
        sink.addSource(source);

        sink.tock(); // 10 items in 1 tick
        // 10 items / 1 tick * 60 = 600 items/min
        // note assertEquals allows you to give it a delta for double comparison (0.001)
        assertEquals(600.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
    }

    /**
     * Tests that itemsPerMinute calculation works correctly across multiple tocks.
     */
    @Test
    void itemsPerMinuteAcrossMultipleTocks() {
        FakeOutputSource source = new FakeOutputSource("Ore", 10);

        Sink sink = new Sink();
        sink.addSource(source);

        int cycles = 5;
        for (int i = 0; i < cycles; i++) {
            sink.tock();
            source.storedAmount = 10; // Refill the source each cycle
        }

        // 50 items in 5 ticks = 10 items/tick * 60 = 600 items/min
        assertEquals(600.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
    }

    /**
     * Tests that itemsPerMinute calculation works correctly across multiple tocks with varying amounts.
     */
    @Test
    void itemsPerMinuteAcrossMultipleTocksAverages() {
        FakeOutputSource source = new FakeOutputSource("Ore", 10);

        Sink sink = new Sink();
        sink.addSource(source);

        int cycles = 5;
        for (int i = 0; i < cycles; i++) {
            sink.tock();
            source.storedAmount = (i+1)*10; // Refill the source each cycle
        }

        // 110 items across 5 ticks = 22 items/tick * 60 = 1320 items/min
        assertEquals(1320.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
    }

    /**
     * Tests that empty tocks still count toward the average items per minute denominator.
     */
    @Test
    void itemsPerMinuteIncludesEmptyTocks() {
        FakeOutputSource source = new FakeOutputSource("Ore", 10);

        Sink sink = new Sink();
        sink.addSource(source);

        sink.tock(); // 10 items
        sink.tock(); // 0 items

        // 10 items across 2 tocks = 5 items/tock * 60 = 300 items/min
        assertEquals(300.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
    }

        /**
     * Tests that averages for different item types are tracked independently
     * across the same set of tocks.
     */
    @Test
    void itemsPerMinuteTrackedIndependentlyPerItemType() {
        FakeOutputSource oreSource = new FakeOutputSource("Ore", 0);
        FakeOutputSource coalSource = new FakeOutputSource("Coal", 0);

        Sink sink = new Sink();
        sink.addSource(oreSource);
        sink.addSource(coalSource);

        int[][] refills = {
            {3, 1},
            {0, 2},
            {1, 0},
            {2, 1}
        };

        for (int[] refill : refills) {
            oreSource.storedAmount = refill[0];
            coalSource.storedAmount = refill[1];
            sink.tock();
        }

        assertEquals(6L, sink.getItemsConsumed("Ore"));
        assertEquals(4L, sink.getItemsConsumed("Coal"));
        assertEquals(90.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
        assertEquals(60.0, sink.getAvgItemsPerMinute("Coal"), 0.001);
    }

    /**
     * Tests that multiple sources with the same item type are aggregated together.
     */
    @Test
    void aggregatesMultipleSourcesOfSameItemType() {
        FakeOutputSource oreSourceA = new FakeOutputSource("Ore", 3);
        FakeOutputSource oreSourceB = new FakeOutputSource("Ore", 2);

        Sink sink = new Sink();
        sink.addSource(oreSourceA);
        sink.addSource(oreSourceB);

        sink.tock();

        assertEquals(5L, sink.getItemsConsumed("Ore"));
        assertEquals(1, sink.getItemTypes().size());
    }

    /**
     * Tests aggregation of the same item type across multiple tocks, including
     * cycles where one source is empty and only the other contributes items.
     */
    @Test
    void aggregatesSameItemTypeAcrossMultipleTocks() {
        FakeOutputSource oreSourceA = new FakeOutputSource("Ore", 0);
        FakeOutputSource oreSourceB = new FakeOutputSource("Ore", 0);

        Sink sink = new Sink();
        sink.addSource(oreSourceA);
        sink.addSource(oreSourceB);

        int[][] refills = {
            {3, 2},
            {1, 0},
            {0, 4},
            {2, 0},
            {0, 0}
        };

        for (int[] refill : refills) {
            oreSourceA.storedAmount = refill[0];
            oreSourceB.storedAmount = refill[1];
            sink.tock();
        }
        assertTrue(sink.getItemTypes().contains("Ore"));
        assertEquals(1, sink.getItemTypes().size());
        assertEquals(12L, sink.getItemsConsumed("Ore"));

    }

        
    /**
     * Tests aggregation across multiple sources providing several different item types.
     */
    @Test
    void aggregatesMultipleSourcesAcrossSeveralItemTypes() {
        FakeOutputSource oreSourceA = new FakeOutputSource("Ore", 3);
        FakeOutputSource oreSourceB = new FakeOutputSource("Ore", 2);
        FakeOutputSource coalSourceA = new FakeOutputSource("Coal", 4);
        FakeOutputSource coalSourceB = new FakeOutputSource("Coal", 1);
        FakeOutputSource wireSource = new FakeOutputSource("Wire", 5);

        Sink sink = new Sink();
        sink.addSource(oreSourceA);
        sink.addSource(oreSourceB);
        sink.addSource(coalSourceA);
        sink.addSource(coalSourceB);
        sink.addSource(wireSource);

        sink.tock();
        assertTrue(sink.getItemTypes().contains("Ore"));
        assertTrue(sink.getItemTypes().contains("Coal"));
        assertTrue(sink.getItemTypes().contains("Wire"));
        assertEquals(3, sink.getItemTypes().size());
        assertEquals(5L, sink.getItemsConsumed("Ore"));
        assertEquals(5L, sink.getItemsConsumed("Coal"));
        assertEquals(5L, sink.getItemsConsumed("Wire"));

    }

    /**
     * Tests that getItemTypes remains a stable set of unique consumed item types over time.
     */
    @Test
    void getItemTypesRemainsStableOverTime() {
        FakeOutputSource oreSource = new FakeOutputSource("Ore", 1);
        FakeOutputSource coalSource = new FakeOutputSource("Coal", 1);

        Sink sink = new Sink();
        sink.addSource(oreSource);
        sink.addSource(coalSource);

        sink.tock();
        assertEquals(2, sink.getItemTypes().size());
        assertTrue(sink.getItemTypes().contains("Ore"));
        assertTrue(sink.getItemTypes().contains("Coal"));

        oreSource.storedAmount = 2;
        sink.tock();

        assertEquals(2, sink.getItemTypes().size());
        assertTrue(sink.getItemTypes().contains("Ore"));
        assertTrue(sink.getItemTypes().contains("Coal"));
    }

    /**
     * Tests that resetStatistics correctly clears all accumulated statistics 
     * and thus resets the sink to an empty state (sink has no persisting state, unlike belt/machine).
     */
    @Test
    void resetStatisticsClearsCounters() {
        FakeOutputSource source = new FakeOutputSource("Ore", 5);

        Sink sink = new Sink();
        sink.addSource(source);

        sink.tock();
        assertEquals(5L, sink.getItemsConsumed("Ore"));

        sink.resetStatistics();
        assertTrue(sink.isEmpty());

        assertEquals(0, sink.getItemsConsumed("Ore"));
        assertEquals(0.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
    }

    /**
     * Tests that resetStatistics clears previous counts and averages so future activity starts fresh.
     */
    @Test
    void resetStatisticsRestartsTrackingFromZero() {
        FakeOutputSource source = new FakeOutputSource("Ore", 5);

        Sink sink = new Sink();
        sink.addSource(source);

        sink.tock();
        // should be empty.

        sink.resetStatistics();

        // resetStatisticsClearsCounters shows getItemsConsumed and getAvgItemsPerMin are 0 here

        source.storedAmount = 2;
        sink.tock();

        assertTrue(sink.getItemTypes().contains("Ore"));
        assertEquals(2L, sink.getItemsConsumed("Ore"));
        assertEquals(1, sink.getItemTypes().size());
        // Check tock reset: 2 items / 1 tock * 60 = 120 items / minute
        assertEquals(120.0, sink.getAvgItemsPerMinute("Ore"), 0.001); 
    }

    /**
     * Tests that connected sources can all remain empty for many tocks still 
     * show no items consumed for sinks.
     */
    @Test
    void handlesAllConnectedSourcesEmptyAcrossMultipleTocks() {
        FakeOutputSource oreSource = new FakeOutputSource("Ore", 0);
        FakeOutputSource coalSource = new FakeOutputSource("Coal", 0);

        Sink sink = new Sink();
        sink.addSource(oreSource);
        sink.addSource(coalSource);

        for (int i = 0; i < 4; i++) {
            sink.tock();
        }

        assertTrue(sink.isEmpty());
        assertTrue(sink.getItemTypes().isEmpty());
        assertEquals(0L, sink.getItemsConsumed("Ore"));
        assertEquals(0L, sink.getItemsConsumed("Coal"));
        assertEquals(0.0, sink.getAvgItemsPerMinute("Ore"), 0.001);
        assertEquals(0.0, sink.getAvgItemsPerMinute("Coal"), 0.001);
    }

    /**
     * Tests that the sink correctly handles the case where it has no sources and tocks
     * without error.
     */
    @Test
    void handlesNoSources() {
        Sink sink = new Sink();
        sink.tock();
        assertTrue(sink.isEmpty());
    }

    // You do not need to understand the section below to write your own tests:
    // This is what is known as a test "fake" which is a very minimal implementation
    // of an output source interface. We know your Sink will pull items from 
    // a class implementing OutputSource (quite possibly more complex than this fake!), 
    // but we don't know what that class is yet so can't use it in the test!
    // Luckily, using interfaces allows us to create modular tests that don't depend on the 
    // implementation of other classes.
    // Your unit tests won't need to do this for other components, 
    // as you can directly construct the components you need to test based on your classes.
    private static final class FakeOutputSource implements OutputSource {
        private final String itemType;
        private int storedAmount;

        FakeOutputSource(String itemType, int storedAmount) {
            this.itemType = itemType;
            this.storedAmount = storedAmount;
        }

        @Override
        public String itemType() {
            return itemType;
        }

        @Override
        public boolean canPull() {
            return storedAmount > 0;
        }

        @Override
        public void pullItem() {
            storedAmount--;
        }
    }
}
