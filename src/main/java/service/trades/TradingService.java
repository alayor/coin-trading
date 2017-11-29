package service.trades;

import service.model.Trade;
import service.model.TradeResult;
import service.trades.tools.CurrentTradesHolder;
import service.trades.tools.TradesRestApiClient;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.Collections.reverse;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private CurrentTradesHolder currentTradesHolder;
    private final TradesRestApiClient tradesRestApiClient;
    private final ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private final ScheduledExecutorService executor;

    private static ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        return scheduledThreadPoolExecutor;
    }

    public TradingService(TradesRestApiClient tradesRestApiClient, TradingSimulator tradingSimulator) {
        this(tradesRestApiClient, getScheduledThreadPoolExecutor(), tradingSimulator);
    }

    TradingService(TradesRestApiClient tradesRestApiClient, ScheduledExecutorService executor, TradingSimulator tradingSimulator) {
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

    void setCurrentTradesHolder(CurrentTradesHolder currentTradesHolder)
    {
        this.currentTradesHolder = currentTradesHolder;
    }
}
