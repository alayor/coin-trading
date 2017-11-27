package service.tools;

import java.util.concurrent.atomic.AtomicInteger;

public class TickCounter {
    private volatile AtomicInteger upticks = new AtomicInteger(0);
    private volatile AtomicInteger downticks = new AtomicInteger(0);

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
