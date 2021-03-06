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

/**
 * Get trades from the client api and loads them in CurrentTradesHolder.
 * It also updates them every certain time (5 seconds).
 */
public class TradingService {
    private static TradingService tradingService;
    private CurrentTradesHolder currentTradesHolder;
    private TradesRestApiClient tradesRestApiClient;
    private ScheduledFuture<?> scheduledFuture;
    private final Runnable updateTradesRunnable = this::updateTrades;
    private ScheduledExecutorService executor;

    private TradingService(
            TradesRestApiClient tradesRestApiClient,
            ScheduledExecutorService executor,
            TradingSimulator tradingSimulator) {
        this.tradesRestApiClient = tradesRestApiClient;
        currentTradesHolder = new CurrentTradesHolder(getTradesFromApi(tradesRestApiClient), tradingSimulator);
        this.executor = executor;
    }

    /**
     * Returns an instance of the service by specifying the simulator that will be used to add simulated trades.
     * @param tradingSimulator The simulator that will be used to add simulated trades.
     * @return a new or existing instance of this class.
     */
    public static TradingService getInstance(TradingSimulator tradingSimulator) {
        if (tradingService == null) {
            tradingService = new TradingService(new TradesRestApiClient(), getScheduledExecutor(), tradingSimulator);
        }
        return tradingService;
    }

    /**
     * Returns an instance of the service by specifying the api client and trades simulator.
     * @param tradesRestApiClient The object that interacts with Bitso(c) REST Api.
     * @param tradingSimulator The simulator that will be used to add simulated trades.
     * @return a new or existing instance of this class.
     */
    public static TradingService getInstance(
      TradesRestApiClient tradesRestApiClient,
      TradingSimulator tradingSimulator) {
        if (tradingService == null) {
            tradingService = new TradingService(tradesRestApiClient, getScheduledExecutor(), tradingSimulator);
        }
        return tradingService;
    }

    /**
     * Starts the process for trades updating.
     */
    public void start() {
        scheduledFuture = executor.scheduleWithFixedDelay(updateTradesRunnable, 5, 5, SECONDS);
    }

    /**
     * Calls the trades holder to get the current trades.
     * @param limit Maximum number of trades to be returned.
     * @return A list of trades according to the limit.
     */
    public List<Trade> getLastTrades(int limit) {
        List<Trade> trades = currentTradesHolder.getTrades();
        return trades.subList(0, limit > trades.size() ? trades.size() : limit);
    }

    /**
     * Stops the trades updating process.
     */
    public void stop() {
        scheduledFuture.cancel(true);
        executor.shutdown();
    }

    private static ScheduledThreadPoolExecutor getScheduledExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        return scheduledThreadPoolExecutor;
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

    /**
     * It should only used for testing.
     */
    public static void clearInstance() {
        tradingService = null;
    }
}
