package service.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import service.model.Trade;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
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
    public void shouldAddOneSimulatedBuyTradeWhenUpticksAreMet() throws Exception
    {
        // given
        Trade lastTrade = createTrade("1", "100");
        List<Trade> newTrades = asList(
                createTrade("2", "200"),
                createTrade("3", "300"),
                createTrade("4", "400")
        );
        givenGetUpticksWillReturnAtCall(3, 2);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(4, trades.size());
    }

    private void givenGetUpticksWillReturnAtCall(int valueToReturn, int callNumber)
    {
        given(tickCounter.getUpticks()).willAnswer(new Answer<Integer>()
        {
            int count = 1;
            @Override public Integer answer(InvocationOnMock invocation) throws Throwable
            {
                if(count == callNumber) {
                    return valueToReturn;
                }
                count++;
                return -1;
            }
        });
    }

    @Test
    public void shouldAddOneSimulatedBuyTradeWhenDownticksAreMet() throws Exception
    {
        // given
        Trade lastTrade = createTrade("1", "400");
        List<Trade> newTrades = asList(
                createTrade("2", "300"),
                createTrade("3", "200"),
                createTrade("4", "100")
        );
        givenGetDownticksWillReturnAtCall(3, 2);
        // when
        List<Trade> trades = trading.addSimulatedTrades(lastTrade, newTrades);
        // then
        assertEquals(4, trades.size());
    }

    private void givenGetDownticksWillReturnAtCall(int valueToReturn, int callNumber)
    {
        given(tickCounter.getDownticks()).willAnswer(new Answer<Integer>()
        {
            int count = 1;
            @Override public Integer answer(InvocationOnMock invocation) throws Throwable
            {
                if(count == callNumber) {
                    return valueToReturn;
                }
                count++;
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
}
