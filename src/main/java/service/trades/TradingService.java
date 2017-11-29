package service.trades;

import service.model.Trade;
import service.model.TradeResult;
import service.tools.CurrentTrades;
import service.trades.tools.TradesApiClient;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.Collections.reverse;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private CurrentTrades currentTrades;
    private final TradesApiClient tradesApiClient;
    private final ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private final ScheduledExecutorService executor;

    private static ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        return scheduledThreadPoolExecutor;
    }

    public TradingService(TradesApiClient tradesApiClient, TradingSimulator tradingSimulator) {
        this(tradesApiClient, getScheduledThreadPoolExecutor(), tradingSimulator);
    }

    TradingService(TradesApiClient tradesApiClient, ScheduledExecutorService executor, TradingSimulator tradingSimulator) {
        this.tradesApiClient = tradesApiClient;
        currentTrades = new CurrentTrades(getTradesFromApi(tradesApiClient), tradingSimulator);
        this.executor = executor;
        scheduledFuture = executor.scheduleWithFixedDelay(updateTradesRunnable, 5, 5, SECONDS);
    }

    private List<Trade> getTradesFromApi(TradesApiClient tradesApiClient) {
        List<Trade> tradeList = tradesApiClient.getTrades(100).getTradeList();
        reverse(tradeList);
        return tradeList;
    }

    void updateTrades() {
        TradeResult tradesSince = tradesApiClient.getTradesSince(currentTrades.getLastTradeId());
        currentTrades.addTrades(tradesSince.getTradeList());
    }

    Runnable getUpdateTradesRunnable() {
        return updateTradesRunnable;
    }

    public void stop() {
        scheduledFuture.cancel(true);
        executor.shutdown();
    }

    public List<Trade> getLastTrades(int limit) {
        List<Trade> trades = currentTrades.getTrades();
        return trades.subList(0, limit > trades.size() ? trades.size() : limit);
    }

    void setCurrentTrades(CurrentTrades currentTrades)
    {
        this.currentTrades = currentTrades;
    }
}
