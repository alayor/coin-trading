package service;

import service.model.Trade;
import service.tools.BitsoApiRequester;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

class TradingService {

    private final BitsoApiRequester bitsoApiRequester;
    private List<Trade> trades;

    TradingService(BitsoApiRequester bitsoApiRequester) {
        this.bitsoApiRequester = bitsoApiRequester;
        trades = bitsoApiRequester.getTrades(100).getTradeList();

        ScheduledExecutorService scheduledExecutorService = newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> getLastTrades(5), 0, 5, TimeUnit.SECONDS);
    }

    List<Trade> getLastTrades(int limit) {
       return trades;
    }
}
