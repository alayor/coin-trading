package service.tools;

import service.model.Trade;

import java.math.BigDecimal;
import java.util.List;

public class SimulatedTrading {

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
//    private void calculateLastTradeTick(List<Trade> tradeList) {
//        BigDecimal lastTradePrice = getPrice(trades.getLast());
//        lastTradePrice.compareTo(tradeList.get(0))
//    }

    private BigDecimal getPrice(Trade trade) {
        return new BigDecimal(trade.getPrice());
    }

    public List<Trade> addSimulatedTrades(List<Trade> tradeList) {

        return tradeList;
    }
}
