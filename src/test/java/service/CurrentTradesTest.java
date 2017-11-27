package service;

import org.junit.Before;
import org.junit.Test;
import service.model.Trade;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class CurrentTradesTest {
    private CurrentTrades currentTrades;

    @Before
    public void setUp() throws Exception {
        currentTrades = new CurrentTrades();
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
          createTrade("1233"),
          createTrade("1244")
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
        currentTrades.addTrades(singletonList(createTrade("501")));
        // then
        List<Trade> trades = currentTrades.getTrades();
        assertEquals(500, trades.size());
        assertEquals("501", trades.get(0).getTid());
    }

    private List<Trade> createTrades(int num) {
        List<Trade> trades = new ArrayList<>();
        for(int i = 0; i < num; i++) {
          trades.add(createTrade(String.valueOf(i)));
        }
        return trades;
    }

    private Trade createTrade(String id) {
        return new Trade(
          "btc_mxn",
          "2017-11-26",
          "100",
          "sell",
          "50",
          id
        );
    }
}