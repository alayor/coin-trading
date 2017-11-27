package service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static service.Tool.createTrade;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import service.TradingSimulator;
import service.tools.TickCounter;
import service.model.Trade;

@RunWith(MockitoJUnitRunner.class)
public class TradingSimulatorTest
{
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
        givenGetUpticksWillReturnAtCall(3,3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(4, trades.size());
    }

    private void givenGetUpticksWillReturnAtCall(int valueToReturn, int callNumber) {
        given(tickCounter.getUpticks()).willAnswer(new Answer<Integer>()
        {
            int count = 0;
            @Override public Integer answer(InvocationOnMock invocation) throws Throwable
            {
                count++;
                if(count == callNumber) {
                    return valueToReturn;
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
        givenGetUpticksWillReturnAtCall(3, 1);
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
        givenGetDownticksWillReturnAtCall(3, 3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(4, trades.size());
    }

    private void givenGetDownticksWillReturnAtCall(int valueToReturn, int callNumber) {
        given(tickCounter.getDownticks()).willAnswer(new Answer<Integer>()
        {
            int count = 0;
            @Override public Integer answer(InvocationOnMock invocation) throws Throwable
            {
                count++;
                if(count == callNumber) {
                    return valueToReturn;
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
        givenGetDownticksWillReturnAtCall(3, 1);
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
        givenGetUpticksWillReturnAtCall(3, 3);
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
        givenGetDownticksWillReturnAtCall(3, 3);
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
        givenGetUpticksWillReturnAtCall(3, 3);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals("sell", trades.get(3).getMakerSide());
    }

    @Test
    public void newDowntickAddedTradeShouldBeBuyMarkerSide() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
                createTrade("2", "200"),
                createTrade("3", "300"),
                createTrade("4", "400", "sell")
        );
        givenGetDownticksWillReturnAtCall(3, 3);
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
        givenGetUpticksWillReturnAtCall(3, 3);
        // when
        trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        verify(tickCounter).reset();
    }

    @Test
    public void shouldResetTickCounterWhenNewBuyTradeIsAdded() throws Exception {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
                createTrade("2", "200"),
                createTrade("3", "300"),
                createTrade("4", "400", "sell"));
        givenGetDownticksWillReturnAtCall(3, 3);
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
        given(tickCounter.getDownticks()).willReturn(0);
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
        given(tickCounter.getUpticks()).willReturn(0);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(3, trades.size());
    }
}
