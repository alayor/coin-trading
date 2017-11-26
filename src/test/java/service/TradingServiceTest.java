package service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.Trade;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TradingServiceTest {

    private TradingService tradingService;
    @Mock
    private BitsoApiRequester bitsoApiRequester;
    @Mock
    private TradeResult tradeResult;
    @Mock
    private ScheduledExecutorService scheduleExecutorService;
    @Mock
    private ScheduledFuture future;

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

    @Test
    public void shouldScheduleTradesUpdatingProcess() throws Exception {
        // when
        tradingService = new TradingService(bitsoApiRequester, scheduleExecutorService);
        // then
        verify(scheduleExecutorService).scheduleAtFixedRate(
          tradingService.getUpdateTradesRunnable(), 0, 5, SECONDS);
    }

    @Test
    public void shouldStopScheduler() throws Exception {
        // given
        doReturn(future).when(scheduleExecutorService).scheduleAtFixedRate(any(), anyLong(), anyLong(), any());
        tradingService = new TradingService(bitsoApiRequester, scheduleExecutorService);
        // when
        tradingService.stop();
        // then
        verify(future).cancel(false);
    }

    @Test
    public void shouldGetLastTradeUsingLastTradeId() throws Exception {
        // given
        given(bitsoApiRequester.getTrades(anyInt())).willReturn(tradeResult);
        List<Trade> trades = asList(
          createTrade("1234"),
          createTrade("6789")
        );
        given(tradeResult.getTradeList()).willReturn(trades);
        tradingService = new TradingService(bitsoApiRequester, scheduleExecutorService);
        // when
        tradingService.updateTrades();
        // then
        verify(bitsoApiRequester).getTradesSince("6789");
    }

    private Trade createTrade(String id) {
        return new Trade(
          "btc_mxn",
          "2017-11-26",
          "100",
          "buy",
          "200",
          id
        );
    }
}