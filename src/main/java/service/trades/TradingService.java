package service.trades;

import service.model.trades.Trade;
import service.model.trades.TradeResult;
import service.trades._tools.holders.CurrentTradesHolder;
import service.trades._tools.rest_client.TradesRestApiClient;
import service.trades._tools.simulator.TradingSimulator;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.Collections.reverse;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {
    private static TradingService tradingService;
    private CurrentTradesHolder currentTradesHolder;
    private TradesRestApiClient tradesRestApiClient;
    private ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private ScheduledExecutorService executor;

    private static ScheduledThreadPoolExecutor getScheduledExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        return scheduledThreadPoolExecutor;
    }

    public static TradingService getInstance(
      TradesRestApiClient tradesRestApiClient,
      TradingSimulator tradingSimulator) {
        if (tradingService == null) {
            tradingService = new TradingService(tradesRestApiClient, getScheduledExecutor(), tradingSimulator);
        }
        return tradingService;
    }

    static TradingService getInstance(
      TradesRestApiClient tradesRestApiClient,
      ScheduledExecutorService executor,
      TradingSimulator tradingSimulator,
      CurrentTradesHolder currentTradesHolder) {
        if (tradingService == null) {
            tradingService = new TradingService(tradesRestApiClient, executor, tradingSimulator);
            tradingService.currentTradesHolder = currentTradesHolder;
        }
        return tradingService;
    }

    private TradingService(TradesRestApiClient tradesRestApiClient, TradingSimulator tradingSimulator) {
        this(tradesRestApiClient, getScheduledExecutor(), tradingSimulator);
    }

    private TradingService(
      TradesRestApiClient tradesRestApiClient,
      ScheduledExecutorService executor,
      TradingSimulator tradingSimulator) {
        this.tradesRestApiClient = tradesRestApiClient;
        currentTradesHolder = new CurrentTradesHolder(getTradesFromApi(tradesRestApiClient), tradingSimulator);
        this.executor = executor;
        scheduledFuture = executor.scheduleWithFixedDelay(updateTradesRunnable, 5, 5, SECONDS);
    }

    private List<Trade> getTradesFromApi(TradesRestApiClient tradesRestApiClient) {
        List<Trade> tradeList = tradesRestApiClient.getTrades(100).getTradeList();
        reverse(tradeList);
        return tradeList;
    }

    void updateTrades() {
        TradeResult tradesSince = tradesRestApiClient.getTradesSince(currentTradesHolder.getLastTradeId());
        currentTradesHolder.addTrades(tradesSince.getTradeList());
    }

    Runnable getUpdateTradesRunnable() {
        return updateTradesRunnable;
    }

    public void stop() {
        scheduledFuture.cancel(true);
        executor.shutdown();
    }

    public List<Trade> getLastTrades(int limit) {
        List<Trade> trades = currentTradesHolder.getTrades();
        return trades.subList(0, limit > trades.size() ? trades.size() : limit);
    }

    public static void clearInstance() {
        tradingService = null;
    }
}
