package service.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import service.model.Trade;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static service.Tool.createTrade;

@RunWith(MockitoJUnitRunner.class)
public class SimulatedTradingTest {
    private SimulatedTrading trading;
    @Mock
    private TickCounter tickCounter;

    @Before
    public void setUp() throws Exception {
        trading = new SimulatedTrading(3, 3);
        trading.setTickCounter(tickCounter);
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
    public void shouldAddOneSimulatedTradeWhenUpticksAreMet() throws Exception
    {
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

    private void givenGetUpticksWillReturnAtCall(int valueToReturn, int callNumber)
    {
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
    public void shouldAddOneSimulatedTradeWhenUpticksMetWithLastTrade() throws Exception
    {
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
    public void shouldAddOneSimulatedTradeWhenDownticksAreMet() throws Exception
    {
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

    private void givenGetDownticksWillReturnAtCall(int valueToReturn, int callNumber)
    {
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
    public void shouldAddOneSimulatedTradeWhenDownticksMetWithLastTrade() throws Exception
    {
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
    public void newUptickAddedTradeShouldBeSimulated() throws Exception
    {
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
    public void newDowntickAddedTradeShouldBeSimulated() throws Exception
    {
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
}
