package service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TradingServiceTest {

    private TradingService tradingService;
    @Mock
    private BitsoApiRequester bitsoApiRequester;
    @Mock
    private TradeResult tradeResult;

    @Before
    public void setUp() throws Exception {
        given(bitsoApiRequester.getTrades(anyInt())).willReturn(tradeResult);
    }

    @Test
    public void shouldGetInitialTrades() throws Exception {
        // when
        tradingService = new TradingService(bitsoApiRequester);
        // then
        verify(bitsoApiRequester).getTrades(100);
    }

}