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

    public List<Trade> addSimulatedTrades(Trade lastTrade, List<Trade> newTrades) {
        List<Trade> tradesWithSimulated = new ArrayList<>();
        tradesWithSimulated.add(newTrades.get(0));
        calculateLastTradeTick(lastTrade, newTrades, tradesWithSimulated);
        for (int i = 1; i < newTrades.size(); i++) {
            tradesWithSimulated.add(newTrades.get(i));
            int compare = getPrice(newTrades.get(i)).compareTo(getPrice(newTrades.get(i - 1)));
            tick(compare);
            addSimulatedTrade(newTrades.get(i), tradesWithSimulated);
        }
        return tradesWithSimulated;
    }

    private void addSimulatedTrade(Trade currentTrade, List<Trade> tradesWithSimulated)
    {
        if(tickCounter.getUpticks() == upticksToSell) {
            tradesWithSimulated.add(currentTrade.withSimulated(true));
        } else if(tickCounter.getDownticks() == downticksToBuy) {
            tradesWithSimulated.add(currentTrade.withSimulated(true));
        }
    }

    private void tick(int compare)
    {
        if (compare > 0) {
            tickCounter.uptick();
        } else if (compare < 0) {
            tickCounter.downtick();
        }
    }

    private void calculateLastTradeTick(Trade lastTrade, List<Trade> tradeList, List<Trade> tradesWithSimulated) {
        BigDecimal lastTradePrice = getPrice(lastTrade);
        int compare = getPrice(tradeList.get(0)).compareTo(lastTradePrice);
        tick(compare);
        addSimulatedTrade(tradeList.get(0), tradesWithSimulated);
    }

    private BigDecimal getPrice(Trade trade) {
        return new BigDecimal(trade.getPrice());
    }

    void setTickCounter(TickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }
}
