package service.trades._tools.holders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.trades.Trade;
import service.trades._tools.simulator.TradingSimulator;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static service.UnitTestTool.createTrade;
import static service.UnitTestTool.createTrades;

@RunWith(MockitoJUnitRunner.class)
public class CurrentTradesHolderTest {
    private CurrentTradesHolder currentTradesHolder;
    @Mock
    private TradingSimulator tradingSimulator;

    @Before
    public void setUp() throws Exception {
        currentTradesHolder = new CurrentTradesHolder(emptyList(), tradingSimulator);
    }

    @Test
    public void shouldNotThrowExceptionIfConstructorArgumentIsNull() throws Exception {
        // when
        currentTradesHolder = new CurrentTradesHolder(null, tradingSimulator);
        // then no exception is thrown
    }

    @Test
    public void shouldNotThrowExceptionIfParameterIsNull() throws Exception {
        // when
        currentTradesHolder.addTrades(null);
        // then no exception is thrown
    }

    @Test
    public void shouldReturnTradesInReverseOrderAsAdded() throws Exception {
        // given
        given(tradingSimulator.addSimulatedTrades(any(), any())).willReturn(asList(
          createTrade("1233", "1000"),
          createTrade("1244", "1000")
        ));
        currentTradesHolder.addTrades(emptyList());
        // when
        List<Trade> trades = currentTradesHolder.getTrades();
        // then
        assertEquals("1244", trades.get(0).getTid());
        assertEquals("1233", trades.get(1).getTid());
    }

    @Test
    public void shouldGetLastTradeId() throws Exception {
        // given
        given(tradingSimulator.addSimulatedTrades(any(), any())).willReturn(asList(
          createTrade("1233", "1000"),
          createTrade("1244", "1000")
        ));
        currentTradesHolder.addTrades(emptyList());
        // when
        String lastTradeId = currentTradesHolder.getLastTradeId();
        // then
        assertEquals("1244", lastTradeId);
    }

    @Test
    public void shouldFreeSpaceFromQueueIfEmpty() throws Exception {
        // given
        currentTradesHolder = new CurrentTradesHolder(createTrades(500), tradingSimulator);
        currentTradesHolder.setTradingSimulator(tradingSimulator);
        given(tradingSimulator.addSimulatedTrades(any(), any())).willReturn(
          singletonList(createTrade("501", "100")));
        // when
        currentTradesHolder.addTrades(emptyList());
        // then
        List<Trade> trades = currentTradesHolder.getTrades();
        assertEquals(500, trades.size());
        assertEquals("501", trades.get(0).getTid());
    }

    @Test
    public void shouldBeCreatedWithInitialTrades() throws Exception {
        // given
        currentTradesHolder = new CurrentTradesHolder(createTrades(3), tradingSimulator);
        // when
        List<Trade> trades = currentTradesHolder.getTrades();
        // then
        assertEquals(3, trades.size());
    }

    @Test
    public void shouldCallContrarianSimulatedTrading() throws Exception {
        // given
        Trade trade = createTrade("1", "100");
        currentTradesHolder = new CurrentTradesHolder(singletonList(trade), tradingSimulator);
        currentTradesHolder.setTradingSimulator(tradingSimulator);
        List<Trade> tradeList = singletonList(createTrade("2", "1002"));
        currentTradesHolder.addTrades(tradeList);
        // when
        currentTradesHolder.getTrades();
        // then
        verify(tradingSimulator).addSimulatedTrades(trade, tradeList);
    }

    @Test
    public void addTradesFromSimulatedTradesToCurrentTrades() throws Exception {
        // given
        currentTradesHolder = new CurrentTradesHolder(emptyList(), tradingSimulator);
        currentTradesHolder.setTradingSimulator(tradingSimulator);
        List<Trade> trades = singletonList(createTrade("2", "200"));
        given(tradingSimulator.addSimulatedTrades(any(), any())).willReturn(trades);
        currentTradesHolder.addTrades(emptyList());
        // when
        List<Trade> actualTrades = currentTradesHolder.getTrades();
        // then
        assertEquals(trades.get(0).getTid(), actualTrades.get(0).getTid());
    }
}
