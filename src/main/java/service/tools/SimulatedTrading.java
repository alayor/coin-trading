package service.tools;

import service.model.Trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SimulatedTrading {

    private final int downticksToBuy;
    private final int upticksToSell;
    private TickCounter tickCounter;

    public SimulatedTrading(int upticksToSell, int downticksToBuy) {
        tickCounter = new TickCounter();
        this.upticksToSell = upticksToSell;
        this.downticksToBuy = downticksToBuy;
    }

    //    private List<Trade> createSimulatedTrades(List<Trade> tradeList) {

    //        if(tradeList == null || tradeList.isEmpty()) {
//            return tradeList;
//        }
//        List<Trade> newTrades = new ArrayList<>();
//        reverse(tradeList);
//        newTrades.add(tradeList.get(0));
//        calculateLastTradeTick(tradeList);
//        for (int i = 1; i < tradeList.size(); i++) {
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
//        reverse(newTrades);
//        return newTrades;
//    }
//


    public List<Trade> addSimulatedTrades(Trade lastTrade, List<Trade> newTrades) {
        List<Trade> tradesWithSimulated = new ArrayList<>();
        tradesWithSimulated.add(newTrades.get(0));
        calculateLastTradeTick(lastTrade, newTrades);
        for (int i = 1; i < newTrades.size(); i++) {
            tradesWithSimulated.add(newTrades.get(i));
            int compare = getPrice(newTrades.get(i)).compareTo(getPrice(newTrades.get(i - 1)));
            tick(compare);
            if(tickCounter.getUpticks() == upticksToSell) {
                tradesWithSimulated.add(newTrades.get(i));
            } else if(tickCounter.getDownticks() == downticksToBuy) {
                tradesWithSimulated.add(newTrades.get(i));
            }
        }
        return tradesWithSimulated;
    }

    private void tick(int compare)
    {
        if (compare > 0) {
            tickCounter.uptick();
        } else if (compare < 0) {
            tickCounter.downtick();
        }
    }

    private void calculateLastTradeTick(Trade lastTrade, List<Trade> tradeList) {
        BigDecimal lastTradePrice = getPrice(lastTrade);
        int compare = getPrice(tradeList.get(0)).compareTo(lastTradePrice);
        tick(compare);
    }

    private BigDecimal getPrice(Trade trade) {
        return new BigDecimal(trade.getPrice());
    }

    void setTickCounter(TickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }
}
