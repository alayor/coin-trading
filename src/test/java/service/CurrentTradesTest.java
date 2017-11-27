package service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import service.model.Trade;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class CurrentTradesTest {
    private CurrentTrades currentTrades;

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
    @Ignore
    public void shouldCreateSellTradeIfUpticksAreMet() throws Exception {
        // given
        currentTrades = new CurrentTrades(createTrades(20), 3, 3);
        currentTrades.addTrades(asList(
          createTrade("1", "1000"),
          createTrade("2", "1001"),
          createTrade("3", "1002")
        ));
        // when
        List<Trade> trades = currentTrades.getTrades();
        // then
        assertEquals(24, trades.size());
    }
}