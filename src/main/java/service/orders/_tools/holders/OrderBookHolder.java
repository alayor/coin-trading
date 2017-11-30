package service.orders._tools.holders;

import service.model.orders.OrderBookResult;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class OrderBookHolder {
    private String minSequence = "";
    private String currentSequence = "";
    private Set<String> concurrentHashSet = ConcurrentHashMap.newKeySet();
    private BlockingQueue<String> bids = new PriorityBlockingQueue<>(1000);
    private BlockingQueue<String> asks = new PriorityBlockingQueue<>(1000);

    public void loadOrderBook(OrderBookResult orderBookResult) {

    }
}
