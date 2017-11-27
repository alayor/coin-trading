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

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static service.Tool.createTrade;

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
    @Mock
    private TradingSimulator tradingSimulator;

    @Before
    public void setUp() throws Exception {
        given(bitsoApiRequester.getTrades(anyInt())).willReturn(tradeResult);
    }

    @Test
    public void shouldGetInitialTrades() throws Exception {
        // when
        tradingService = new TradingService(bitsoApiRequester, tradingSimulator);
        // then
        verify(bitsoApiRequester).getTrades(100);
    }

    @Test
    public void shouldScheduleTradesUpdatingProcess() throws Exception {
        // when
        tradingService = new TradingService(bitsoApiRequester, scheduleExecutorService, tradingSimulator);
        // then
        verify(scheduleExecutorService).scheduleWithFixedDelay(
          tradingService.getUpdateTradesRunnable(), 5, 5, SECONDS);
    }

    @Test
    public void shouldStopScheduler() throws Exception {
        // given
        doReturn(future).when(scheduleExecutorService).scheduleWithFixedDelay(
          any(), anyLong(), anyLong(), any());
        tradingService = new TradingService(bitsoApiRequester, scheduleExecutorService, tradingSimulator);
        // when
        tradingService.stop();
        // then
        verify(future).cancel(false);
    }

    @Test
    public void shouldAddNewTradesToArrayQueue() throws Exception {
        // given
        given(bitsoApiRequester.getTradesSince(anyString())).willReturn(tradeResult);
        List<Trade> newTrades = singletonList(createTrade("6789", "100"));
        given(tradeResult.getTradeList()).willReturn(newTrades);
        given(tradingSimulator.addSimulatedTrades(any(), anyListOf(Trade.class))).willReturn(newTrades);
        tradingService = new TradingService(bitsoApiRequester, scheduleExecutorService, tradingSimulator);
        tradingService.updateTrades();
        // when
        List<Trade> lastTrades = tradingService.getLastTrades();
        // then
        assertEquals(2, lastTrades.size());
    }
}
