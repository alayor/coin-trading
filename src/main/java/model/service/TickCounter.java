package model.service;

import java.util.concurrent.atomic.AtomicInteger;

public class TickCounter {
    private AtomicInteger upticks = new AtomicInteger(0);
    private AtomicInteger downticks = new AtomicInteger(0);

    void uptick() {
        upticks.incrementAndGet();
        downticks.set(0);
    }

    void downtick() {
        downticks.incrementAndGet();
        upticks.set(0);
    }

    int getUpticks() {
        return upticks.get();
    }

    int getDownticks() {
        return downticks.get();
    }
}
