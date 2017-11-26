package service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TradingServiceTest {

    private TradingService tradingService;
    @Mock
    private BitsoApiRequester bitsoApiRequester;
    @Mock
    private TradeResult tradeResult;

    @Test
    public void shouldUseBitsoClientToGetTradesIfCurrentTradesAreEmpty() throws Exception {
        //given
        tradingService = new TradingService(bitsoApiRequester);
        // when
        tradingService.getLastTrades(5);
        // then
        verify(bitsoApiRequester).getTrades(5);
    }

    @Test
    public void shouldNoyUseBitsoClientToGetTradesIfCurrentTradesAreNotEmpty() throws Exception {
        // given
        given(bitsoApiRequester.getTrades(anyInt())).willReturn(tradeResult);
        tradingService = new TradingService(bitsoApiRequester);
        tradingService.getLastTrades(5);
        // when
        tradingService.getLastTrades(5);
        // then
        verify(bitsoApiRequester, times(1)).getTrades(5);
    }
}