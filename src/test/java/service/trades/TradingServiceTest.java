package service.trades;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.Trade;
import service.model.TradeResult;
import service.trades.tools.CurrentTradesHolder;
import service.trades.tools.TradesRestApiClient;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static service.UnitTestTool.createTrade;
import static service.UnitTestTool.createTrades;

@RunWith(MockitoJUnitRunner.class)
public class TradingServiceTest {

    private TradingService tradingService;
    @Mock
    private TradesRestApiClient tradesRestApiClient;
    @Mock
    private TradeResult tradeResult;
    @Mock
    private ScheduledExecutorService scheduleExecutorService;
    @Mock
    private ScheduledFuture future;
    @Mock
    private TradingSimulator tradingSimulator;
    @Mock
    private CurrentTradesHolder currentTradesHolder;

    @Before
    public void setUp() throws Exception {
        TradingService.clearInstance();
        given(tradesRestApiClient.getTrades(anyInt())).willReturn(tradeResult);
    }

    @Test
    public void shouldGetInitialTrades() throws Exception {
        // given
        TradesRestApiClient localTradesRestApiClient = mock(TradesRestApiClient.class);
        given(localTradesRestApiClient.getTrades(anyInt())).willReturn(tradeResult);
        // when
        tradingService = TradingService.getInstance(localTradesRestApiClient, scheduleExecutorService, tradingSimulator, currentTradesHolder);
        // then
        verify(localTradesRestApiClient).getTrades(100);
    }

    @Test
    public void shouldScheduleTradesUpdatingProcess() throws Exception {
        // when
        initializeTradesService();
        // then
        verify(scheduleExecutorService).scheduleWithFixedDelay(
          tradingService.getUpdateTradesRunnable(), 5, 5, SECONDS);
    }

    @Test
    public void shouldStopScheduler() throws Exception {
        // given
        doReturn(future).when(scheduleExecutorService).scheduleWithFixedDelay(
          any(), anyLong(), anyLong(), any());
        initializeTradesService();
        // when
        tradingService.stop();
        // then
        verify(future).cancel(true);
    }

    @Test
    public void shouldShutdownExecutor() throws Exception {
        // given
        doReturn(future).when(scheduleExecutorService).scheduleWithFixedDelay(
          any(), anyLong(), anyLong(), any());
        initializeTradesService();
        // when
        tradingService.stop();
        // then
        verify(scheduleExecutorService).shutdown();
    }

    @Test
    public void shouldAddNewTradesToCurrentTrades() throws Exception {
        // given
        initializeTradesService();
        given(tradesRestApiClient.getTradesSince(anyString())).willReturn(tradeResult);
        List<Trade> newTrades = singletonList(createTrade("6789", "100"));
        given(tradeResult.getTradeList()).willReturn(newTrades);
        // when
        tradingService.updateTrades();
        // then
        verify(currentTradesHolder).addTrades(newTrades);
    }

    @Test
    public void shouldReturnTradesBasedOnLimit() throws Exception {
        // given
        initializeTradesService();
        given(currentTradesHolder.getTrades()).willReturn(createTrades(30));
        // when
        List<Trade> lastTrades = tradingService.getLastTrades(10);
        // then
        assertEquals(10, lastTrades.size());
    }

    @Test
    public void shouldReturnTradesBasedOnLimitValidatingMaxLimit() throws Exception {
        // given
        initializeTradesService();
        given(currentTradesHolder.getTrades()).willReturn(createTrades(5));
        // when
        List<Trade> lastTrades = tradingService.getLastTrades(10);
        // then
        assertEquals(5, lastTrades.size());
    }

    @Test
    public void shouldReturnTradesBasedOnLimitIfLimitIsSameAsSize() throws Exception {
        // given
        initializeTradesService();
        given(currentTradesHolder.getTrades()).willReturn(createTrades(10));
        // when
        List<Trade> lastTrades = tradingService.getLastTrades(10);
        // then
        assertEquals(10, lastTrades.size());
    }

    private void initializeTradesService() {
        tradingService = TradingService.getInstance(
          tradesRestApiClient,
          scheduleExecutorService,
          tradingSimulator,
          currentTradesHolder);
    }

}
