package service.$tools;

import org.junit.Before;
import org.junit.Test;
import service.UnitTestTool.AsyncTester;
import service.trades._tools.simulator.TickCounter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TickCounterTest {

    private TickCounter tickCounter;

    @Before
    public void setUp() throws Exception {
        tickCounter = TickCounter.getInstance();
    }

    @Test
    public void uptickShouldIncrementUptickCount() throws Exception {
        // when
        int uptick = tickCounter.uptick();
        // then
        assertEquals(1, uptick);
    }

    @Test
    public void downTickShouldIncrementDowntickCount() throws Exception {
        // when
        int downtick = tickCounter.downtick();
        // then
        assertEquals(1, downtick);
    }

    @Test
    public void uptickShouldResetDownticksCount() throws Exception {
        // given
        tickCounter.downtick();
        tickCounter.downtick();
        //when
        tickCounter.uptick();
        // then
        assertEquals(1, tickCounter.downtick());
    }

    @Test
    public void downtickShouldResetUpticksCount() throws Exception {
        // given
        tickCounter.uptick();
        tickCounter.uptick();
        // when
        tickCounter.downtick();
        // then
        assertEquals(1, tickCounter.uptick());
    }

    @Test
    public void shouldUptickAtomically() throws Exception {
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                // given
                for (int i1 = 0; i1 < 1000000; i1++) {
                    // when
                    int uptick1 = tickCounter.uptick();
                    int uptick2 = tickCounter.uptick();
                    //then
                    assertTrue(uptick2 > uptick1);
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldDowntickAtomically() throws Exception {
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                // given
                for (int i1 = 0; i1 < 1000000; i1++) {
                    // when
                    int downtick1 = tickCounter.downtick();
                    int downtick2 = tickCounter.downtick();
                    //then
                    assertTrue(downtick2 > downtick1);
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldClearUpticksAtomically() throws Exception {
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this) {
                    // given
                    tickCounter.uptick();
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        // when
                        tickCounter.downtick();
                        //then
                        assertEquals(1, tickCounter.uptick());
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
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this) {
                    // given
                    tickCounter.downtick();
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        // when
                        tickCounter.uptick();
                        //then
                        assertEquals(1, tickCounter.downtick());
                    }
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }

    @Test
    public void shouldResetUpticks() throws Exception {
        // given
        tickCounter.uptick();
        tickCounter.uptick();
        // when
        tickCounter.reset();
        // then
        assertEquals(1, tickCounter.uptick());
    }

    @Test
    public void shouldResetDownticks() throws Exception {
        // given
        tickCounter.downtick();
        tickCounter.downtick();
        // when
        tickCounter.reset();
        // then
        assertEquals(1, tickCounter.downtick());
    }

    @Test
    public void shouldResetUpticksAtomically() throws Exception {
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this) {
                    // given
                    tickCounter.uptick();
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        // when
                        tickCounter.reset();
                        //then
                        assertEquals(1, tickCounter.uptick());
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
        int threadsCount = 5;
        AsyncTester[] testers = new AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new AsyncTester(() -> {
                synchronized (this) {
                    // given
                    tickCounter.downtick();
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        // when
                        tickCounter.reset();
                        //then
                        assertEquals(1, tickCounter.downtick());
                    }
                }
            });
            testers[i].start();
        }
        for (AsyncTester tester : testers)
            tester.test();
    }
}
