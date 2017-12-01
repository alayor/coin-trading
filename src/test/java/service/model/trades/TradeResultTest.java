package service.model.trades;

import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertTrue;

public class TradeResultTest {
    @Test
    public void shouldInitializeValues() throws Exception {
        // when
        TradeResult tradeResult = new TradeResult(true, emptyList());
        // then
        assertTrue(tradeResult.isSuccess());
        assertTrue(tradeResult.getTradeList().isEmpty());
    }
}