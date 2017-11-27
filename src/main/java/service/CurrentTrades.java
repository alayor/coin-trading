package service;

import service.model.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Collections.reverse;

class CurrentTrades {
    private final BlockingDeque<Trade> trades = new LinkedBlockingDeque<>(500);
    private TradingSimulator simulatedTrading;

    CurrentTrades(List<Trade> trades, int upticksToSell, int downticksToBuy) {
        simulatedTrading = new TradingSimulator(upticksToSell, downticksToBuy);
        freeSpaceAndAddTrades(trades);
    }

    void addTrades(List<Trade> tradeList) {
        tradeList = simulatedTrading.addSimulatedTrades(getLast(), tradeList);
        freeSpaceAndAddTrades(tradeList);
    }

    private Trade getLast() {
        if (trades.isEmpty()) {
           return Trade.NULL;
        } else {
            return trades.getLast();
        }
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

    List<Trade> getTrades() {
        List<Trade> list = new ArrayList<>(trades);
        reverse(list);
        return list;
    }

    String getLastTradeId() {
        return getLast().getTid();
    }

    void setSimulatedTrading(TradingSimulator simulatedTrading) {
        this.simulatedTrading = simulatedTrading;
    }
}
