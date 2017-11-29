package service.trades.tools;

import java.util.concurrent.atomic.AtomicInteger;

public class TickCounter {
    private static final TickCounter tickCounter = new TickCounter();
    private volatile AtomicInteger upticks = new AtomicInteger(0);
    private volatile AtomicInteger downticks = new AtomicInteger(0);

    private TickCounter() {}

    public static TickCounter getInstance() {
        return tickCounter;
    }

    public synchronized int uptick() {
        downticks.set(0);
        return upticks.incrementAndGet();
    }

    public synchronized int downtick() {
        upticks.set(0);
        return downticks.incrementAndGet();
    }

    public synchronized void reset()
    {
        upticks.set(0);
        downticks.set(0);
    }
}
