package service.tools;

import java.util.concurrent.atomic.AtomicInteger;

public class TickCounter {
    private AtomicInteger upticks = new AtomicInteger(0);
    private AtomicInteger downticks = new AtomicInteger(0);

    public synchronized void uptick() {
        upticks.incrementAndGet();
        downticks.set(0);
    }

    public synchronized void downtick() {
        downticks.incrementAndGet();
        upticks.set(0);
    }

    public int getUpticks() {
        return upticks.get();
    }

    public int getDownticks() {
        return downticks.get();
    }

    public synchronized void reset()
    {
        upticks.set(0);
        downticks.set(0);
    }
}
