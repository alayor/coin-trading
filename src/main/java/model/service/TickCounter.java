package model.service;

import java.util.concurrent.atomic.AtomicInteger;

public class TickCounter {
    private AtomicInteger upticks = new AtomicInteger(0);
    private AtomicInteger downticks = new AtomicInteger(0);

    void uptick() {
        synchronized (this) {
            upticks.incrementAndGet();
            downticks.set(0);
        }
    }

    void downtick() {
        synchronized (this) {
            downticks.incrementAndGet();
            upticks.set(0);
        }
    }

    int getUpticks() {
        return upticks.get();
    }

    int getDownticks() {
        return downticks.get();
    }
}
