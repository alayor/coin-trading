package service.trades._tools.simulator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds the current upticks and downticks necessary to add simulated trades.
 */
public class TickCounter {
    private static final TickCounter tickCounter = new TickCounter();
    private volatile AtomicInteger upticks = new AtomicInteger(0);
    private volatile AtomicInteger downticks = new AtomicInteger(0);

    private TickCounter() {}

    /**
     * Returns the only instance of this class.
     * @return the singleton instance of this class.
     */
    public static TickCounter getInstance() {
        return tickCounter;
    }

    /**
     * Increases the number of upticks by one and set the number of downticks to zero.
     * @return the new upticks count.
     */
    public synchronized int uptick() {
        downticks.set(0);
        return upticks.incrementAndGet();
    }

    /**
     * Increases the number of downticks by one and set the number of upticks to zero.
     * @return the new downticks count.
     */
    public synchronized int downtick() {
        upticks.set(0);
        return downticks.incrementAndGet();
    }

    /**
     * Set both upticks and downticks to zero.
     */
    public synchronized void reset()
    {
        upticks.set(0);
        downticks.set(0);
    }
}
