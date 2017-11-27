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
            compareAndTick(tradeList.get(0), lastTrade);
            addSimulatedTrade(tradeList.get(0), tradesWithSimulated);
        }
    }

    private void compareAndTick(Trade trade, Trade lastTrade)
    {
        int compare = getPrice(trade).compareTo(getPrice(lastTrade));
        if (compare > 0) {
            tickCounter.uptick();
        } else if (compare < 0) {
            tickCounter.downtick();
        }
    }

    private BigDecimal getPrice(Trade trade) {
        return new BigDecimal(trade.getPrice());
    }

    private void addSimulatedTrade(Trade currentTrade, List<Trade> tradesWithSimulated)
    {
        if(tickCounter.getUpticks() == upticksToSell) {
            tradesWithSimulated.add(currentTrade.withSimulatedAndMarkerSide(true, "sell"));
        } else if(tickCounter.getDownticks() == downticksToBuy) {
            tradesWithSimulated.add(currentTrade.withSimulatedAndMarkerSide(true, "buy"));
        }
    }

    private void addSimulatedTradesFromNewTrades(List<Trade> tradesWithSimulated, List<Trade> newTrades)
    {
        for (int i = 1; i < newTrades.size(); i++) {
            tradesWithSimulated.add(newTrades.get(i));
            compareAndTick(newTrades.get(i), newTrades.get(i - 1));
            addSimulatedTrade(newTrades.get(i), tradesWithSimulated);
        }
    }

    void setTickCounter(TickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }
}
