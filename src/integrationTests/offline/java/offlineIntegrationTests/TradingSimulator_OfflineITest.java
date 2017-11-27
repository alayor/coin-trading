/* Copyright 2017 Sabre Holdings */
package offlineIntegrationTests;

import static java.util.Arrays.asList;
import static offlineIntegrationTests.tools.TraderCreator.createTrade;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import service.TradingSimulator;
import service.model.Trade;

public class TradingSimulator_OfflineITest
{
    private TradingSimulator tradingSimulator;

    @Before
    public void setUp() throws Exception
    {

    }

    @Test
    public void shouldNotReturnSimulatedTradeIfUpticksLessThanUpticksToSell() throws Exception
    {
        // given
        tradingSimulator = new TradingSimulator(3, 3);
        List<Trade> newTrades = asList(
                createTrade("2", "100"),
                createTrade("3", "101"),
                createTrade("4", "100"),
                createTrade("5", "101")
        );
        // when
        List<Trade> trades = tradingSimulator.addSimulatedTrades(createTrade("1", "100"), newTrades);
        // then
        assertEquals(4, trades.size());
    }

    @Test
    public void shouldAddSellSimulatedTradeIfUpticksEqualThanUpticksToSell() throws Exception
    {
        // given
        tradingSimulator = new TradingSimulator(3, 3);
        List<Trade> newTrades = asList(
                createTrade("2", "101", "buy"),
                createTrade("3", "102", "buy"),
                createTrade("4", "103", "buy"),
                createTrade("5", "102", "buy")
        );
        // when
        List<Trade> trades = tradingSimulator.addSimulatedTrades(createTrade("1", "100"), newTrades);
        // then
        assertEquals(5, trades.size());
        assertTrue(trades.get(3).isSimulated());
        assertEquals("sell", trades.get(3).getMakerSide());
    }

    @Test
    public void shouldAddBuySimulatedTradeIfDownticksEqualThanDownticksToBuy() throws Exception
    {
        // given
        tradingSimulator = new TradingSimulator(3, 2);
        List<Trade> newTrades = asList(
                createTrade("2", "102", "sell"),
                createTrade("3", "101", "sell"),
                createTrade("4", "100", "sell")
        );
        // when
        List<Trade> trades = tradingSimulator.addSimulatedTrades(createTrade("1", "103"), newTrades);
        // then
        assertEquals(4, trades.size());
        assertTrue(trades.get(2).isSimulated());
        assertEquals("buy", trades.get(2).getMakerSide());
    }

    @Test
    public void shouldAddMultipleSellSimulatedTradeIfUpticksEqualThanUpticksToSell() throws Exception
    {
        // given
        tradingSimulator = new TradingSimulator(2, 3);
        List<Trade> newTrades = asList(
                createTrade("2", "101", "buy"),
                createTrade("3", "102", "buy"),
                createTrade("4", "103", "buy"),
                createTrade("5", "104", "buy")
        );
        // when
        List<Trade> trades = tradingSimulator.addSimulatedTrades(createTrade("1", "100"), newTrades);
        // then
        assertEquals(6, trades.size());
        assertTrue(trades.get(2).isSimulated());
        assertEquals("sell", trades.get(2).getMakerSide());
        assertTrue(trades.get(5).isSimulated());
        assertEquals("sell", trades.get(5).getMakerSide());
    }

}
