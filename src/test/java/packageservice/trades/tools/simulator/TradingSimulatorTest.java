package packageservice.trades.tools.simulator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import service.UnitTestTool;
import service.model.trades.Trade;
import service.trades._tools.simulator.TickCounter;
import service.trades._tools.simulator.TradingSimulator;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static service.UnitTestTool.createTrade;

@RunWith(MockitoJUnitRunner.class)
public class TradingSimulatorTest {
    private TradingSimulator trading;
    @Mock
    private TickCounter tickCounter;

    @Before
    public void setUp() throws Exception {
        trading = new TradingSimulator(3, 3);
        trading.setTickCounter(tickCounter);
    }

    @Test
    public void shouldNotThrowExceptionIfLastTradeIsNull() throws Exception {
        // when
        trading.addSimulatedTrades(null, singletonList(createTrade("1", "100")));
        // then no exception is thrown
    }

    @Test
    public void shouldUptickThreeTimes() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
          createTrade("2", "200"),
          createTrade("3", "300"),
          createTrade("4", "400")
        );
        // when
        trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        verify(tickCounter, times(3)).uptick();
    }

    @Test
    public void shouldAddOneSimulatedTradeWhenUpticksAreMet() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
          createTrade("2", "200"),
          createTrade("3", "300"),
          createTrade("4", "400")
        );
        givenGetUpticksWillReturnAtCall(3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(4, trades.size());
    }

    private void givenGetUpticksWillReturnAtCall(int callNumber) {
        given(tickCounter.uptick()).willAnswer(new Answer<Integer>() {
            int count = 0;

            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                count++;
                if (count == callNumber) {
                    return 3;
                }
                return -1;
            }
        });
    }

    @Test
    public void shouldAddOneSimulatedTradeWhenUpticksMetWithLastTrade() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = singletonList(
          createTrade("2", "200")
        );
        givenGetUpticksWillReturnAtCall(1);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(2, trades.size());
    }

    @Test
    public void shouldAddOneSimulatedTradeWhenDownticksAreMet() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "400");
        List<Trade> newTrades = asList(
          createTrade("2", "300"),
          createTrade("3", "200"),
          createTrade("4", "100")
        );
        givenGetDownticksWillReturnAtCall(3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(4, trades.size());
    }

    private void givenGetDownticksWillReturnAtCall(int callNumber) {
        given(tickCounter.downtick()).willAnswer(new Answer<Integer>() {
            int count = 0;

            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                count++;
                if (count == callNumber) {
                    return 3;
                }
                return -1;
            }
        });
    }

    @Test
    public void shouldDowntickThreeTimes() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "400");
        List<Trade> newTrades = asList(
          createTrade("2", "300"),
          createTrade("3", "200"),
          createTrade("4", "100")
        );
        // when
        trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        verify(tickCounter, times(3)).downtick();
    }

    @Test
    public void shouldAddOneSimulatedTradeWhenDownticksMetWithLastTrade() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "200");
        List<Trade> newTrades = singletonList(
          createTrade("2", "100")
        );
        givenGetDownticksWillReturnAtCall(1);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(2, trades.size());
    }

    @Test
    public void newUptickAddedTradeShouldBeSimulated() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
          createTrade("2", "200"),
          createTrade("3", "300"),
          createTrade("4", "400")
        );
        givenGetUpticksWillReturnAtCall(3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertTrue(trades.get(3).isSimulated());
    }

    @Test
    public void newDowntickAddedTradeShouldBeSimulated() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "400");
        List<Trade> newTrades = asList(
          createTrade("2", "300"),
          createTrade("3", "200"),
          createTrade("4", "100")
        );
        givenGetDownticksWillReturnAtCall(3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertTrue(trades.get(3).isSimulated());
    }

    @Test
    public void newUptickAddedTradeShouldBeSellMarkerSide() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
          createTrade("2", "200"),
          createTrade("3", "300"),
          createTrade("4", "400", "buy")
        );
        givenGetUpticksWillReturnAtCall(3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals("sell", trades.get(3).getMakerSide());
    }

    @Test
    public void newDowntickAddedTradeShouldBeBuyMarkerSide() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "400");
        List<Trade> newTrades = asList(
          createTrade("2", "300"),
          createTrade("3", "200"),
          createTrade("4", "100", "sell")
        );
        givenGetDownticksWillReturnAtCall(3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals("buy", trades.get(3).getMakerSide());
    }

    @Test
    public void shouldResetTickCounterWhenNewSellTradeIsAdded() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
          createTrade("2", "200"),
          createTrade("3", "300"),
          createTrade("4", "400", "buy")
        );
        givenGetUpticksWillReturnAtCall(3);
        // when
        trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        verify(tickCounter).reset();
    }


    @Test
    public void shouldResetTickCounterAtomically() throws Exception {
        // given
        trading = new TradingSimulator(1, 1);
        trading.setTickCounter(tickCounter);
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = singletonList(createTrade("2", "200"));
        given(tickCounter.uptick()).willReturn(1);
        int threadsCount = 5;
        UnitTestTool.AsyncTester[] testers = new UnitTestTool.AsyncTester[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            testers[i] = new UnitTestTool.AsyncTester(() -> {
                for (int i1 = 0; i1 < 100; i1++) {
                    // when
                    trading.addSimulatedTrades(lastTrade, newTrades);
                }
            });
            testers[i].start();
        }
        for (UnitTestTool.AsyncTester tester : testers)
            tester.test();

        //then
        InOrder inOrder = inOrder(tickCounter);
        inOrder.verify(tickCounter, atLeastOnce()).uptick();
        inOrder.verify(tickCounter, atLeastOnce()).reset();
    }


    @Test
    public void shouldResetTickCounterWhenNewBuyTradeIsAdded() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "400");
        List<Trade> newTrades = asList(
          createTrade("2", "300"),
          createTrade("3", "200"),
          createTrade("4", "100", "sell"));
        givenGetDownticksWillReturnAtCall(3);
        // when
        trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        verify(tickCounter).reset();
    }

    @Test
    public void shouldNotAddSimulatedTradeIfDownticksToBuyIsZero() throws Exception {
        // given
        trading = new TradingSimulator(3, 0);
        trading.setTickCounter(tickCounter);
        Trade lastTrade = createTrade("1", "400");
        List<Trade> newTrades = asList(
          createTrade("2", "400"),
          createTrade("3", "400"),
          createTrade("4", "400")
        );
        given(tickCounter.downtick()).willReturn(0);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(3, trades.size());
    }

    @Test
    public void shouldNotAddSimulatedTradeIfUpticksToSellIsZero() throws Exception {
        // given
        trading = new TradingSimulator(0, 3);
        trading.setTickCounter(tickCounter);
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
          createTrade("2", "200"),
          createTrade("3", "300"),
          createTrade("4", "400")
        );
        given(tickCounter.uptick()).willReturn(0);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(3, trades.size());
    }
}
