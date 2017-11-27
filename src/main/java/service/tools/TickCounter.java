package service.tools;

import java.util.concurrent.atomic.AtomicInteger;

public class TickCounter {
    private AtomicInteger upticks = new AtomicInteger(0);
    private AtomicInteger downticks = new AtomicInteger(0);

    public void uptick() {
        synchronized (this) {
            upticks.incrementAndGet();
            downticks.set(0);
        }
    }

    public void downtick() {
        synchronized (this) {
            downticks.incrementAndGet();
            upticks.set(0);
        }
    }

    public int getUpticks() {
        return upticks.get();
    }

    public int getDownticks() {
        return downticks.get();
    }
}
