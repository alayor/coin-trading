package service.trades;

import service.model.Trade;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;
import service.tools.CurrentTrades;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.Collections.reverse;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private CurrentTrades currentTrades;
    private final BitsoApiRequester bitsoApiRequester;
    private final ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private final ScheduledExecutorService executor;

    private static ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        return scheduledThreadPoolExecutor;
    }

    public TradingService(BitsoApiRequester bitsoApiRequester, TradingSimulator tradingSimulator) {
        this(bitsoApiRequester, getScheduledThreadPoolExecutor(), tradingSimulator);
    }

    TradingService(BitsoApiRequester bitsoApiRequester, ScheduledExecutorService executor, TradingSimulator tradingSimulator) {
        this.bitsoApiRequester = bitsoApiRequester;
        currentTrades = new CurrentTrades(getTradesFromApi(bitsoApiRequester), tradingSimulator);
        this.executor = executor;
        scheduledFuture = executor.scheduleWithFixedDelay(updateTradesRunnable, 5, 5, SECONDS);
    }

    private List<Trade> getTradesFromApi(BitsoApiRequester bitsoApiRequester) {
        List<Trade> tradeList = bitsoApiRequester.getTrades(100).getTradeList();
        reverse(tradeList);
        return tradeList;
    }

    void updateTrades() {
        TradeResult tradesSince = bitsoApiRequester.getTradesSince(currentTrades.getLastTradeId());
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
