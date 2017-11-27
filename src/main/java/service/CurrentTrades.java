package service;

import service.model.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Collections.reverse;

class CurrentTrades {
    private final BlockingDeque<Trade> tradeDeque = new LinkedBlockingDeque<>(500);

    void addTrades(List<Trade> tradeList) {
        if(tradeList != null) {
            freeSpace(tradeList);
            tradeDeque.addAll(tradeList);
        }
    }

    private void freeSpace(List<Trade> tradeList) {
        int tradesToPoll = tradeList.size() - tradeDeque.remainingCapacity();
        if(tradesToPoll > 0) {
            for (int i = 0; i < tradesToPoll; i++) {
                tradeDeque.poll();
            }
        }
    }

    List<Trade> getTrades() {
        List<Trade> list = new ArrayList<>(tradeDeque);
        reverse(list);
        return list;
    }

    String getLastTradeId() {
        return tradeDeque.peek().getTid();
    }
}
