package service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.Trade;
import service.tools.SimulatedTrading;
import service.tools.TickCounter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CurrentTradesTest {
    private CurrentTrades currentTrades;
    @Mock
    private TickCounter tickCounter;
    @Mock
    private SimulatedTrading simulatedTrading;

    @Before
    public void setUp() throws Exception {
        currentTrades = new CurrentTrades(emptyList(), 3, 3);
    }

    @Test
    public void shouldNotThrowExceptionIfConstructorArgumentIsNull() throws Exception {
        // when
        currentTrades = new CurrentTrades(null, 3, 3);
        // then no exception is thrown
    }

    @Test
    public void shouldNotThrowExceptionIfParameterIsNull() throws Exception {
        // when
        currentTrades.addTrades(null);
        // then no exception is thrown
    }

    @Test
    public void shouldReturnTradesInReverseOrderAsAdded() throws Exception {
        // given
        currentTrades.addTrades(asList(
          createTrade("1233", "1000"),
          createTrade("1244", "1000")
        ));
        // when
        List<Trade> trades = currentTrades.getTrades();
        // then
        assertEquals("1244", trades.get(0).getTid());
        assertEquals("1233", trades.get(1).getTid());
    }

    @Test
    public void shouldGetLastTradeId() throws Exception {
        // given
        currentTrades.addTrades(asList(
          createTrade("1233", "1000"),
          createTrade("1244", "1000")
        ));
        // when
        String lastTradeId = currentTrades.getLastTradeId();
        // then
        assertEquals("1244", lastTradeId);
    }

    @Test
    public void shouldFreeSpaceFromQueueIfEmpty() throws Exception {
        // given
        currentTrades.addTrades(createTrades(500));
        // when
        currentTrades.addTrades(singletonList(createTrade("501", "1000")));
        // then
        List<Trade> trades = currentTrades.getTrades();
        assertEquals(500, trades.size());
        assertEquals("501", trades.get(0).getTid());
    }

    private List<Trade> createTrades(int num) {
        List<Trade> trades = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            trades.add(createTrade(String.valueOf(i), "1000"));
        }
        return trades;
    }

    private Trade createTrade(String id, String price) {
        return new Trade(
          "btc_mxn",
          "2017-11-26",
          "100",
          "sell",
          price,
          id
        );
    }

    @Test
    public void shouldBeCreatedWithInitialTrades() throws Exception {
        // given
        currentTrades = new CurrentTrades(createTrades(3), 3, 3);
        // when
        List<Trade> trades = currentTrades.getTrades();
        // then
        assertEquals(3, trades.size());
    }

    @Test
    public void shouldCallContrarianSimulatedTrading() throws Exception {
        // given
        currentTrades = new CurrentTrades(emptyList(), 3, 3);
        currentTrades.setSimulatedTrading(simulatedTrading);
        List<Trade> tradeList = singletonList(createTrade("2", "1002"));
        currentTrades.addTrades(tradeList);
        // when
        currentTrades.getTrades();
        // then
        verify(simulatedTrading).addSimulatedTrades(tradeList);
    }

    @Test
    public void addTradesFromSimulatedTradesToCurrentTrades() throws Exception {
        // given
        currentTrades = new CurrentTrades(emptyList(), 3, 3);
        currentTrades.setSimulatedTrading(simulatedTrading);
        List<Trade> trades = singletonList(createTrade("1", "100"));
        given(simulatedTrading.addSimulatedTrades(emptyList())).willReturn(trades);
        currentTrades.addTrades(emptyList());
        // when
        List<Trade> actualTrades = currentTrades.getTrades();
        // then
        assertEquals(trades.get(0).getTid(), actualTrades.get(0).getTid());
    }
}