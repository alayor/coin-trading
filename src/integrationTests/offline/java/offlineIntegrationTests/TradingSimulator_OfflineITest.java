package offlineIntegrationTests;

import org.junit.Before;
import org.junit.Test;
import service.model.trades.Trade;
import service.trades.TradingSimulator;

import java.util.List;

import static java.util.Arrays.asList;
import static offlineIntegrationTests.tools.TraderCreator.createTrade;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TradingSimulator_OfflineITest
{
    private TradingSimulator tradingSimulator;

    @Before
    public void setUp() throws Exception {
        tradingSimulator = new TradingSimulator(3, 3);
        tradingSimulator.resetCounter();
    }

    @Test
    public void shouldNotReturnSimulatedTradeIfUpticksLessThanUpticksToSell() throws Exception
    {
        // given
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

    private TradingSimulator getTradingSimulator(int upticksToSell, int downticksToBuy) {
        return new TradingSimulator(upticksToSell, downticksToBuy);
    }

    @Test
    public void shouldAddSellSimulatedTradeIfUpticksEqualToUpticksToSell() throws Exception
    {
        // given
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
    public void shouldAddBuySimulatedTradeIfDownticksEqualToDownticksToBuy() throws Exception
    {
        // given
        tradingSimulator.setDownticksToBuy(2);
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
    public void shouldAddMultipleSellSimulatedTradeIfUpticksEqualToUpticksToSell() throws Exception
    {
        // given
        tradingSimulator.setUpticksToSell(2);
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

    @Test
    public void shouldAddMultipleBuySimulatedTradeIfUpticksEqualToDownticksToBuy() throws Exception
    {
        // given
        tradingSimulator.setDownticksToBuy(2);
        List<Trade> newTrades = asList(
                createTrade("2", "103", "sell"),
                createTrade("3", "102", "sell"),
                createTrade("4", "101", "sell"),
                createTrade("5", "100", "sell")
        );
        // when
        List<Trade> trades = tradingSimulator.addSimulatedTrades(createTrade("1", "104"), newTrades);
        // then
        assertEquals(6, trades.size());
        assertTrue(trades.get(2).isSimulated());
        assertEquals("buy", trades.get(2).getMakerSide());
        assertTrue(trades.get(5).isSimulated());
        assertEquals("buy", trades.get(5).getMakerSide());
    }

    @Test
    public void shouldNotAddAnyTradesIfUpticksToSellIsZero() throws Exception
    {
        // given
        tradingSimulator.setUpticksToSell(0);
        tradingSimulator.setDownticksToBuy(2);
        List<Trade> newTrades = asList(
                createTrade("2", "100", "buy"),
                createTrade("3", "100", "buy"),
                createTrade("4", "100", "buy"),
                createTrade("5", "100", "buy")
        );
        // when
        List<Trade> trades = tradingSimulator.addSimulatedTrades(createTrade("1", "100"), newTrades);
        // then
        assertEquals(4, trades.size());
    }

    @Test
    public void shouldNotAddAnyTradesIfDownticksToBuyIsZero() throws Exception
    {
        // given
        tradingSimulator.setUpticksToSell(2);
        tradingSimulator.setDownticksToBuy(0);
        List<Trade> newTrades = asList(
                createTrade("2", "100", "buy"),
                createTrade("3", "100", "buy"),
                createTrade("4", "100", "buy"),
                createTrade("5", "100", "buy")
        );
        // when
        List<Trade> trades = tradingSimulator.addSimulatedTrades(createTrade("1", "100"), newTrades);
        // then
        assertEquals(4, trades.size());
    }
}
