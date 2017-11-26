package service;

import service.model.Trade;
import service.tools.BitsoApiRequester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private final BitsoApiRequester bitsoApiRequester;
    private ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private final ArrayBlockingQueue<Trade> trades = new ArrayBlockingQueue<>(2000);

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

    private void updateTrades() {

    }

    Runnable getUpdateTradesRunnable() {
        return updateTradesRunnable;
    }

    public void stop() {
        scheduledFuture.cancel(false);
    }

    public List<Trade> getLastTrades() {
        List<Trade> list = new ArrayList<>();
        trades.drainTo(list);
        return list;
    }
}
