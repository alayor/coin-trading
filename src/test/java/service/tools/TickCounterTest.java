package service.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TickCounterTest {

    @Test
    public void uptickShouldIncrementUptickCount() throws Exception {
        // when
        TickCounter tickCounter = new TickCounter();
        tickCounter.uptick();
        // then
        assertEquals(1, tickCounter.getUpticks());
    }

    @Test
    public void downTickShouldIncrementDowntickCount() throws Exception {
        // when
        TickCounter tickCounter = new TickCounter();
        tickCounter.downtick();
        // then
        assertEquals(1, tickCounter.getDownticks());
    }

    @Test
    public void uptickShouldResetDownticksCount() throws Exception {
        // given
        TickCounter tickCounter = new TickCounter();
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
        TickCounter tickCounter = new TickCounter();
        tickCounter.uptick();
        tickCounter.uptick();
        // when
        tickCounter.downtick();
        // then
        assertEquals(0, tickCounter.getUpticks());
    }

    @Test
    public void shouldUptickAtomically() throws Exception {
        TickCounter tickCounter = new TickCounter();
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this)
                {
                    // given
                    tickCounter.uptick();
                    int last = tickCounter.getUpticks();
                    for (int i1 = 0; i1 < 1000000; i1++)
                    {
                        // when
                        tickCounter.uptick();
                        int value = tickCounter.getUpticks();
                        //then
                        assertTrue(value > last);
                        last = value;
                    }
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldDowntickAtomically() throws Exception {
        TickCounter tickCounter = new TickCounter();
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this)
                {
                    // given
                    tickCounter.downtick();
                    int last = tickCounter.getDownticks();
                    for (int i1 = 0; i1 < 1000000; i1++)
                    {
                        // when
                        tickCounter.downtick();
                        int value = tickCounter.getDownticks();
                        //then
                        assertTrue(value > last);
                        last = value;
                    }
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldClearUpticksAtomically() throws Exception {
        TickCounter tickCounter = new TickCounter();
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this)
                {
                    // given
                    tickCounter.uptick();
                    for (int i1 = 0; i1 < 1000000; i1++)
                    {
                        // when
                        tickCounter.downtick();
                        //then
                        assertEquals(0, tickCounter.getUpticks());
                    }
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldClearDownticksAtomically() throws Exception {
        TickCounter tickCounter = new TickCounter();
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this)
                {
                    // given
                    tickCounter.downtick();
                    for (int i1 = 0; i1 < 1000000; i1++)
                    {
                        // when
                        tickCounter.uptick();
                        //then
                        assertEquals(0, tickCounter.getDownticks());
                    }
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

    @Test
    public void shouldResetUpticks() throws Exception
    {
        // given
        TickCounter tickCounter = new TickCounter();
        tickCounter.uptick();
        tickCounter.uptick();
        // when
        tickCounter.reset();
        // then
        assertEquals(0, tickCounter.getUpticks());
    }

    @Test
    public void shouldResetDownticks() throws Exception
    {
        // given
        TickCounter tickCounter = new TickCounter();
        tickCounter.downtick();
        tickCounter.downtick();
        // when
        tickCounter.reset();
        // then
        assertEquals(0, tickCounter.getDownticks());
    }

    @Test
    public void shouldResetUpticksAtomically() throws Exception {
        TickCounter tickCounter = new TickCounter();
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this)
                {
                    // given
                    tickCounter.uptick();
                    for (int i1 = 0; i1 < 1000000; i1++)
                    {
                        // when
                        tickCounter.reset();
                        //then
                        assertEquals(0, tickCounter.getUpticks());
                    }
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldResetDownticksAtomically() throws Exception {
        TickCounter tickCounter = new TickCounter();
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this)
                {
                    // given
                    tickCounter.downtick();
                    for (int i1 = 0; i1 < 1000000; i1++)
                    {
                        // when
                        tickCounter.reset();
                        //then
                        assertEquals(0, tickCounter.getDownticks());
                    }
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }
}
