package service.orders._tools.holders;

import service.model.orders.Ask;
import service.model.orders.Bid;
import service.model.orders.OrderBookResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class OrderBookHolder {
    private static OrderBookHolder orderBookHolder;
    private String minSequence = "";
    private String currentSequence = "";
    private Set<String> concurrentHashSet = ConcurrentHashMap.newKeySet();
    private BlockingQueue<Bid> priorityBids = new PriorityBlockingQueue<>(1000);
    private BlockingQueue<Ask> priorityAsks = new PriorityBlockingQueue<>(1000);

    private OrderBookHolder() {

    }

    public void loadOrderBook(OrderBookResult orderBookResult) {
        loadAsks(orderBookResult.getOrderBook().getAsks());
        loadBids(orderBookResult.getOrderBook().getBids());
    }

    private void loadAsks(List<Ask> asks) {
        priorityAsks.addAll(asks);
    }

    private void loadBids(List<Bid> bids) {
        priorityBids.addAll(bids);
    }

    public static OrderBookHolder getInstance() {
        if (orderBookHolder == null) {
            orderBookHolder = new OrderBookHolder();
        }
        return orderBookHolder;
    }

    public List<Ask> getBestAsks(int limit) {
        List<Ask> bestAsks = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            Ask ask = priorityAsks.poll();
            if (ask != null) {
                bestAsks.add(ask);
            }
        }
        priorityAsks.addAll(bestAsks);
        return bestAsks;
    }

    public List<Bid> getBestBids(int limit) {
        List<Bid> bestBids = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            Bid bid = priorityBids.poll();
            if (bid != null) {
                bestBids.add(bid);
            }
        }
        priorityBids.addAll(bestBids);
        return bestBids;
    }

    void clear() {
        priorityBids.clear();
        priorityAsks.clear();
    }
}
