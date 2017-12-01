package service.trades._tools.holders;

import service.model.trades.Trade;
import service.trades._tools.simulator.TradingSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Collections.reverse;

/**
 * Holds the current trades containing all latest trades as well as simulated trades with a limit
 * of 500 hundred.
 */
public class CurrentTradesHolder {
    private final BlockingDeque<Trade> trades = new LinkedBlockingDeque<>(500);
    private TradingSimulator tradingSimulator;

    /**
     * Creates a holder specifying initial trade list and the simulator which will be used to
     * add simulated trades according to its rules.
     * @param newTrades initial trades to be loaded to the internal queue.
     * @param tradingSimulator to be used to add new simulated buy and sell trades.
     */
    public CurrentTradesHolder(List<Trade> newTrades, TradingSimulator tradingSimulator) {
        this.tradingSimulator = tradingSimulator;
        freeSpaceAndAddTrades(newTrades);
    }

    /**
     * Add new trades to the queue calculating first if new simulated trades are going to be added.
     * @param tradeList are the trades to be added to the queue.
     */
    public void addTrades(List<Trade> tradeList) {
        try {
            tradeList = tradingSimulator.addSimulatedTrades(getLast(), tradeList);
            freeSpaceAndAddTrades(tradeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retuns a list with all the trades. The more recent trades will appear first.
     * @return a list of all the current trades.
     */
    public List<Trade> getTrades() {
        List<Trade> list = new ArrayList<>(trades);
        reverse(list);
        return list;
    }

    /**
     * If the trade queue is not empty retrieves the last trade added. Otherwise, returns null trade object.
     * @return the last trade added.
     */
    private Trade getLast() {
        if (trades.isEmpty()) {
            return Trade.NULL;
        } else {
            return trades.getLast();
        }
    }

    /**
     * Returns the id from the last added trade.
     * @return the id from the last added trade.
     */
    public String getLastTradeId() {
        return getLast().getTid();
    }

    private void freeSpaceAndAddTrades(List<Trade> tradeList) {
        if (tradeList != null) {
            freeSpace(tradeList);
            trades.addAll(tradeList);
        }
    }

    private void freeSpace(List<Trade> tradeList) {
        int tradesToPoll = tradeList.size() - trades.remainingCapacity();
        if (tradesToPoll > 0) {
            for (int i = 0; i < tradesToPoll; i++) {
                trades.poll();
            }
        }
    }

    void setTradingSimulator(TradingSimulator tradingSimulator) {
        this.tradingSimulator = tradingSimulator;
    }
}
