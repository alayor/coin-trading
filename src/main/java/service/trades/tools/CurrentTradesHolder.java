package service.trades.tools;

import service.model.Trade;
import service.trades.TradingSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Collections.reverse;

public class CurrentTradesHolder {
    private final BlockingDeque<Trade> trades = new LinkedBlockingDeque<>(500);
    private TradingSimulator tradingSimulator;

    public CurrentTradesHolder(List<Trade> newTrades, TradingSimulator tradingSimulator) {
        this.tradingSimulator = tradingSimulator;
        freeSpaceAndAddTrades(newTrades);
    }

    public void addTrades(List<Trade> tradeList) {
        tradeList = tradingSimulator.addSimulatedTrades(getLast(), tradeList);
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

    public List<Trade> getTrades() {
        List<Trade> list = new ArrayList<>(trades);
        reverse(list);
        return list;
    }

    public String getLastTradeId() {
        return getLast().getTid();
    }

    void setTradingSimulator(TradingSimulator tradingSimulator) {
        this.tradingSimulator = tradingSimulator;
    }
}
