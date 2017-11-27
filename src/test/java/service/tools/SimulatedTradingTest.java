package service.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.Trade;

import java.util.List;

import static java.util.Arrays.asList;
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