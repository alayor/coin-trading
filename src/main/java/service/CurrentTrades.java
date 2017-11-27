package service;

import service.model.Trade;
import service.tools.SimulatedTrading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Collections.reverse;

class CurrentTrades {
    private final BlockingDeque<Trade> trades = new LinkedBlockingDeque<>(500);
    private final int upticksToSell;
    private final int downticksToBuy;
    private SimulatedTrading simulatedTrading = new SimulatedTrading();

    CurrentTrades(List<Trade> trades, int upticksToSell, int downticksToBuy) {
        this.upticksToSell = upticksToSell;
        this.downticksToBuy = downticksToBuy;
        freeSpaceAndAddTrades(trades);
    }

    void addTrades(List<Trade> tradeList) {
        tradeList = simulatedTrading.addSimulatedTrades(tradeList);
        freeSpaceAndAddTrades(tradeList);
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
        return trades.getLast().getTid();
    }

    void setSimulatedTrading(SimulatedTrading simulatedTrading) {
        this.simulatedTrading = simulatedTrading;
    }
}
