package service;

import service.model.Trade;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private final BitsoApiRequester bitsoApiRequester;
    private ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private ArrayBlockingQueue<Trade> trades = new ArrayBlockingQueue<>(2000);

    public TradingService(BitsoApiRequester bitsoApiRequester) {
        this(bitsoApiRequester, newScheduledThreadPool(1));
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
