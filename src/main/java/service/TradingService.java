package service;

import service.model.Trade;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Collections.reverse;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingService {

    private final BitsoApiRequester bitsoApiRequester;
    private final ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private final BlockingDeque<Trade> trades = new LinkedBlockingDeque<>(500);

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
        List<Trade> tradesFromApi = getTradesFromApi(bitsoApiRequester);
        this.trades.addAll(tradesFromApi);
        scheduledFuture = executor.scheduleWithFixedDelay(updateTradesRunnable, 5, 5, SECONDS);
    }

    private List<Trade> getTradesFromApi(BitsoApiRequester bitsoApiRequester) {
        List<Trade> tradeList = bitsoApiRequester.getTrades(100).getTradeList();
        reverse(tradeList);
        return tradeList;
    }

    void updateTrades() {
        TradeResult tradesSince = bitsoApiRequester.getTradesSince(trades.peekLast().getTid());
        List<Trade> tradeList = tradesSince.getTradeList();
        freeSpace(tradeList);
        trades.addAll(tradeList);
    }

    private void freeSpace(List<Trade> tradeList) {
        int tradesToPoll = tradeList.size() - trades.remainingCapacity();
        if(tradesToPoll > 0) {
            for (int i = 0; i < tradesToPoll; i++) {
                trades.poll();
            }
        }
    }

    Runnable getUpdateTradesRunnable() {
        return updateTradesRunnable;
    }

    public void stop() {
        scheduledFuture.cancel(false);
    }

    public List<Trade> getLastTrades() {
        List<Trade> list = new ArrayList<>(trades);
        reverse(list);
        return list;
    }
}
