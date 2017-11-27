package service;

import service.model.Trade;
import service.tools.TickCounter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class TradingSimulator
{
    private final int downticksToBuy;
    private final int upticksToSell;
    private TickCounter tickCounter;

    public TradingSimulator(int upticksToSell, int downticksToBuy) {
        tickCounter = new TickCounter();
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
        if(lastTrade != null) {
            compareAndAddSimulatedTrade(tradeList.get(0), lastTrade, tradesWithSimulated);
        }
    }

    private void compareAndAddSimulatedTrade(Trade trade, Trade lastTrade, List<Trade> tradesWithSimulated)
    {
        int compare = getPrice(trade).compareTo(getPrice(lastTrade));
        if (compare > 0) {
            int uptick = tickCounter.uptick();
            if(upticksToSell > 0 && uptick == upticksToSell) {
                tradesWithSimulated.add(trade.withSimulatedAndMarkerSide(true, "sell"));
                tickCounter.reset();
            }
        } else if (compare < 0) {
            int downtick = tickCounter.downtick();
            if(downticksToBuy > 0 && downtick == downticksToBuy) {
                tradesWithSimulated.add(trade.withSimulatedAndMarkerSide(true, "buy"));
                tickCounter.reset();
            }
        }
    }

    private BigDecimal getPrice(Trade trade) {
        return new BigDecimal(trade.getPrice());
    }

    private void addSimulatedTradesFromNewTrades(List<Trade> tradesWithSimulated, List<Trade> newTrades)
    {
        for (int i = 1; i < newTrades.size(); i++) {
            tradesWithSimulated.add(newTrades.get(i));
            compareAndAddSimulatedTrade(newTrades.get(i), newTrades.get(i - 1), tradesWithSimulated);
        }
    }

    void setTickCounter(TickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }
}
