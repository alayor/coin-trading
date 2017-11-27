package model.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TickCounterTest {
    private TickCounter tickCounter;

    @Before
    public void setUp() throws Exception {
        tickCounter = new TickCounter();
    }

    @Test
    public void uptickShouldIncrementUptickCount() throws Exception {
        // when
        tickCounter.uptick();
        // then
        assertEquals(1, tickCounter.getUpticks());
    }

    @Test
    public void downTickShouldIncrementDowntickCount() throws Exception {
        // when
        tickCounter.downtick();
        // then
        assertEquals(1, tickCounter.getDownticks());
    }

    @Test
    public void uptickShouldResetDownticksCount() throws Exception {
        // given
        tickCounter.downtick();
        tickCounter.downtick();
        //when
        tickCounter.uptick();
        // then
        assertEquals(0, tickCounter.getDownticks());
    }

    @Test
    public void downtickShouldResetUpticksCount() throws Exception {
        // given
        tickCounter.uptick();
        tickCounter.uptick();
        // when
        tickCounter.downtick();
        // then
        assertEquals(0, tickCounter.getUpticks());
    }

    @Test
    public void shouldUptickAtomically() throws Exception {
        // given
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                // when
                tickCounter.uptick();
                int last = tickCounter.getUpticks();
                for (int i1 = 0; i1 < 1000000; i1++) {
                    tickCounter.uptick();
                    int value = tickCounter.getUpticks();
                    //then
                    assertTrue(value > last);
                    last = value;
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldDowntickAtomically() throws Exception {
        // given
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                // when
                tickCounter.downtick();
                int last = tickCounter.getDownticks();
                for (int i1 = 0; i1 < 1000000; i1++) {
                    tickCounter.downtick();
                    int value = tickCounter.getDownticks();
                    //then
                    assertTrue(value > last);
                    last = value;
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    private static class AsyncTester {
        private Thread thread;
        private volatile Error error;
        private volatile RuntimeException runtimeExc;

        AsyncTester(final Runnable runnable) {
            thread = new Thread(() -> {
                try {
                    runnable.run();
                } catch (Error e) {
                    error = e;
                } catch (RuntimeException e) {
                    runtimeExc = e;
                }
            });
        }

        void start() {
            thread.start();
        }

        void test() throws InterruptedException {
            thread.join();
            if (error != null)
                throw error;
            if (runtimeExc != null)
                throw runtimeExc;
        }
    }
}