package service;

import model.service.TickCounter;
import service.model.Trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Collections.reverse;

class CurrentTrades {
    private final BlockingDeque<Trade> trades = new LinkedBlockingDeque<>(500);
    private final int upticksToSell;
    private final int downticksToBuy;
    private final TickCounter tickCounter;

    CurrentTrades(List<Trade> trades, int upticksToSell, int downticksToBuy) {
        this.upticksToSell = upticksToSell;
        this.downticksToBuy = downticksToBuy;
        this.tickCounter = new TickCounter();
        freeSpaceAndAddTrades(trades);
    }

    void addTrades(List<Trade> trades) {
        freeSpaceAndAddTrades(trades);
    }

    private void freeSpaceAndAddTrades(List<Trade> tradeList) {
        if(tradeList != null) {
            tradeList = createSimulatedTrades(tradeList);
            freeSpace(tradeList);
            trades.addAll(tradeList);
        }
    }

    private List<Trade> createSimulatedTrades(List<Trade> tradeList) {
//        List<Trade> newTrades = new ArrayList<>();
//        newTrades.add(tradeList.get(0));
//        for (int i = 0; i < tradeList.size(); i++) {
//            int compare = getPrice(tradeList.get(i)).compareTo(getPrice(tradeList.get(i - 1)));
//            if(compare > 0) {
//                tickCounter.uptick();
//            } else if(compare < 0) {
//                tickCounter.downtick();
//            }
//            newTrades.add(tradeList.get(i));
//            if(tickCounter.getUpticks() == upticksToSell) {
//                // create trade to sell
//            } else if (tickCounter.getDownticks() == downticksToBuy) {
//                // create trade to buy
//            }
//        }
//        return newTrades;
        return tradeList;
    }

    private BigDecimal getPrice(Trade trade) {
        return new BigDecimal(trade.getPrice());
    }

    private void freeSpace(List<Trade> tradeList) {
        int tradesToPoll = tradeList.size() - trades.remainingCapacity();
        if(tradesToPoll > 0) {
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
        return trades.peek().getTid();
    }
}
