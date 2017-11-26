package service;

import service.model.Trade;
import service.tools.BitsoApiRequester;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private final BitsoApiRequester bitsoApiRequester;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private List<Trade> trades;

    public TradingService(BitsoApiRequester bitsoApiRequester) {
        this(bitsoApiRequester, newScheduledThreadPool(1));
    }

    TradingService(BitsoApiRequester bitsoApiRequester, ScheduledExecutorService scheduledExecutorService) {
        this.bitsoApiRequester = bitsoApiRequester;
        trades = bitsoApiRequester.getTrades(100).getTradeList();
        scheduledExecutorService.scheduleAtFixedRate(updateTradesRunnable, 0, 5, SECONDS);
    }

    void updateTrades() {
        bitsoApiRequester.getTradesSince(10);
    }

    Runnable getUpdateTradesRunnable() {
        return updateTradesRunnable;
    }
}
