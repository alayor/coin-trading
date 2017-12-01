package service.model.trades;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TradeTest {
    private Trade trade;

    @Before
    public void setUp() throws Exception {
        trade = new Trade(
          "book",
          "createdAt",
          "10",
          "buy",
          "100",
          "321"
        );
    }

    @Test
    public void shouldInitializeFields() throws Exception {
        assertEquals("book", trade.getBook());
        assertEquals("createdAt", trade.getCreatedAt());
        assertEquals("10", trade.getAmount());
        assertEquals("buy", trade.getMakerSide());
        assertEquals("100", trade.getPrice());
        assertEquals("321", trade.getTid());
        assertFalse(trade.isSimulated());
    }

    @Test
    public void shouldCreateSimulatedTrade() throws Exception {
        // when
        Trade simulatedTrade = this.trade.withSimulatedAndMarkerSide(true, "sell");
        // then
        assertTrue(simulatedTrade.isSimulated());
        assertEquals("sell", simulatedTrade.getMakerSide());
    }
}