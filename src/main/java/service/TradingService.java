package service;

import service.model.Trade;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private final BitsoApiRequester bitsoApiRequester;
    private ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private ArrayBlockingQueue<Trade> trades = new ArrayBlockingQueue<>(2000);

    private static ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        return scheduledThreadPoolExecutor;
    }

    public TradingService(BitsoApiRequester bitsoApiRequester) {
        this(bitsoApiRequester, getScheduledThreadPoolExecutor());
    }

    TradingService(BitsoApiRequester bitsoApiRequester, ScheduledExecutorService executor) {
        this.bitsoApiRequester = bitsoApiRequester;
        trades.addAll(bitsoApiRequester.getTrades(100).getTradeList());
        scheduledFuture = executor.scheduleAtFixedRate(updateTradesRunnable, 0, 5, SECONDS);
    }

    void updateTrades() {
        TradeResult tradesSince = bitsoApiRequester.getTradesSince(10);
    }

    Runnable getUpdateTradesRunnable() {
        return updateTradesRunnable;
    }

    public void stop() {
        scheduledFuture.cancel(false);
    }
}
