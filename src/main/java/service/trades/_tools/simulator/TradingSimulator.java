package service.trades._tools.simulator;

import service.model.trades.Trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Uses contrarian trading algorithm to add simulated trades to the current trades got from Bitso.
 * This strategy will work by counting the M consecutive upticks and N consecutive downticks.
 * A trade that executes at a price that is the same as the price of the trade that executed immediately
 * preceding it is known as a “zero tick”.
 * An uptick is when a trade executes at a higher price than the most recent non-zero-tick trade before it.
 * A downtick is when a trade executes at a lower price than the most recent non-zero-tick trade before it.
 * After M consecutive upticks, the algorithm should sell 1 BTC at the price of the most recent uptick.
 * After N consecutive downticks, it should buy 1 BTC at the price of the most recent downtick.
 */
public class TradingSimulator {
    private int downticksToBuy;
    private int upticksToSell;
    private TickCounter tickCounter;

    /**
     * Creates a Trading Simulator specifying the upticks necessary to create a sell trade as well as
     * the downticks necessary to add a buy trade.
     * @param upticksToSell number of upticks that will make a sell trade be inserted in the current trades
     * @param downticksToBuy number of downticks that will make a buy trade be inserted in the current trades
     */
    public TradingSimulator(int upticksToSell, int downticksToBuy) {
        tickCounter = TickCounter.getInstance();
        this.upticksToSell = upticksToSell;
        this.downticksToBuy = downticksToBuy;
    }

    /**
     * Tries to add a buy or sell trade evaluating the current upticks and downticks and comparing them
     * against upticksToSell and downticksToBuy respectively.
     * If an uptick is detected , then the previous downticks are restarted and vice versa.
     * @param lastTrade is the last trade added to the current trades used to compare against the first new trade.
     * @param newTrades are the new trades retrieved from Bitso Rest Api service.
     * @return the list of trades containing all new trades and any new simulated trade that may be added
     * using the contrarian algorithm regarding upticks and downticks.
     */
    public List<Trade> addSimulatedTrades(Trade lastTrade, List<Trade> newTrades) {
        if (newTrades != null && !newTrades.isEmpty()) {
            List<Trade> tradesWithSimulated = new ArrayList<>(singletonList(newTrades.get(0)));
            addSimulatedTradeFromLastTrade(tradesWithSimulated, lastTrade, newTrades);
            addSimulatedTradesFromNewTrades(tradesWithSimulated, newTrades);
            return tradesWithSimulated;
        }
        return emptyList();
    }

    /**
     * Updates the number of upticks necessary to add a sell trade to the current trades.
     * @param upticksToSell is the number of upticks necessary to add a sell trade to the current trades
     */
    public void setUpticksToSell(int upticksToSell) {
        this.upticksToSell = upticksToSell;
    }

    /**
     * Updates the number of downticks necessary to add a buy trade to the current trades.
     * @param downticksToBuy is the number of downticks necessary to add a buy trade to the current trades
     */
    public void setDownticksToBuy(int downticksToBuy) {
        this.downticksToBuy = downticksToBuy;
    }

    private void addSimulatedTradeFromLastTrade(List<Trade> tradesWithSimulated, Trade lastTrade, List<Trade> tradeList) {
        if (lastTrade != null) {
            compareAndAddSimulatedTrade(tradeList.get(0), lastTrade, tradesWithSimulated);
        }
    }

    private void compareAndAddSimulatedTrade(Trade trade, Trade lastTrade, List<Trade> tradesWithSimulated) {
        int compare = getPrice(trade).compareTo(getPrice(lastTrade));
        Lock lock = new ReentrantLock();
        if (compare > 0) {
            addSellTrade(trade, tradesWithSimulated, lock);
        } else if (compare < 0) {
            addBuyTrade(trade, tradesWithSimulated, lock);
        }
    }

    private void addSellTrade(Trade trade, List<Trade> tradesWithSimulated, Lock lock) {
        try {
            lock.lock();
            if (upticksToSell > 0 && tickCounter.uptick() == upticksToSell) {
                tradesWithSimulated.add(trade.withSimulatedAndMarkerSide(true, "sell"));
                tickCounter.reset();
            }
        } finally {
            lock.unlock();
        }
    }

    private void addBuyTrade(Trade trade, List<Trade> tradesWithSimulated, Lock lock) {
        try {
            lock.lock();
            if (downticksToBuy > 0 && tickCounter.downtick() == downticksToBuy) {
                tradesWithSimulated.add(trade.withSimulatedAndMarkerSide(true, "buy"));
                tickCounter.reset();
            }
        } finally {
            lock.unlock();
        }
    }

    private BigDecimal getPrice(Trade trade) {
        return new BigDecimal(trade.getPrice());
    }

    private void addSimulatedTradesFromNewTrades(List<Trade> tradesWithSimulated, List<Trade> newTrades) {
        for (int i = 1; i < newTrades.size(); i++) {
            tradesWithSimulated.add(newTrades.get(i));
            compareAndAddSimulatedTrade(newTrades.get(i), newTrades.get(i - 1), tradesWithSimulated);
        }
    }

    void setTickCounter(TickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }

    /**
     * To be used only for tests.
     */
    public void resetCounter() {
        tickCounter.reset();
    }
}
