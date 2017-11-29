package service.trades._tools.simulator;

import service.model.trades.Trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Collections.singletonList;

public class TradingSimulator {
    private int downticksToBuy;
    private int upticksToSell;
    private TickCounter tickCounter;

    public TradingSimulator(int upticksToSell, int downticksToBuy) {
        tickCounter = TickCounter.getInstance();
        this.upticksToSell = upticksToSell;
        this.downticksToBuy = downticksToBuy;
    }

    public List<Trade> addSimulatedTrades(Trade lastTrade, List<Trade> newTrades) {
        List<Trade> tradesWithSimulated = new ArrayList<>(singletonList(newTrades.get(0)));
        addSimulatedTradeFromLastTrade(tradesWithSimulated, lastTrade, newTrades);
        addSimulatedTradesFromNewTrades(tradesWithSimulated, newTrades);
        return tradesWithSimulated;
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

    private void addBuyTrade(Trade trade, List<Trade> tradesWithSimulated, Lock lock) {
        try {
            lock.lock();
            int downtick = tickCounter.downtick();
            if (downticksToBuy > 0 && downtick == downticksToBuy) {
                tradesWithSimulated.add(trade.withSimulatedAndMarkerSide(true, "buy"));
                tickCounter.reset();
            }
        } finally {
            lock.unlock();
        }
    }

    private void addSellTrade(Trade trade, List<Trade> tradesWithSimulated, Lock lock) {
        try {
            lock.lock();
            int uptick = tickCounter.uptick();
            if (upticksToSell > 0 && uptick == upticksToSell) {
                tradesWithSimulated.add(trade.withSimulatedAndMarkerSide(true, "sell"));
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

    public void setTickCounter(TickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }

    public void resetCounter() {
        tickCounter.reset();
    }

    public void setDownticksToBuy(int downticksToBuy) {
        this.downticksToBuy = downticksToBuy;
    }

    public void setUpticksToSell(int upticksToSell) {
        this.upticksToSell = upticksToSell;
    }
}
