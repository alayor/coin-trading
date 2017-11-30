package service.orders._tools.holders;

import service.model.diff_orders.DiffOrder;
import service.model.diff_orders.DiffOrderResult;
import service.model.orders.Ask;
import service.model.orders.Bid;
import service.model.orders.OrderBookResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Integer.parseInt;

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
        this.currentSequence = orderBookResult.getOrderBook().getSequence();
        this.minSequence = orderBookResult.getOrderBook().getSequence();
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

    public void applyDiffOrder(DiffOrderResult diffOrderResult) {
        if (parseInt(diffOrderResult.getSequence()) >= parseInt(this.minSequence)) {
            this.currentSequence = diffOrderResult.getSequence();
            applyToBidsOrAsks(diffOrderResult);
        }
    }

    private void applyToBidsOrAsks(DiffOrderResult diffOrderResult) {
        for (DiffOrder diffOrder : diffOrderResult.getDiffOrderList()) {
            if ("0".equals(diffOrder.getType())) {
                applyOrderToBids(diffOrderResult, diffOrder);
            } else if ("1".equals(diffOrder.getType())) {
                applyOrderToAsks(diffOrderResult, diffOrder);
            }
        }
    }

    private void applyOrderToBids(DiffOrderResult diffOrderResult, DiffOrder diffOrder) {
        if (diffOrder.getAmount().length() > 1) {
            priorityBids.add(new Bid(
              diffOrderResult.getBook(),
              diffOrder.getOrderId(),
              diffOrder.getRate(),
              diffOrder.getAmount()
            ));
        }
    }

    private void applyOrderToAsks(DiffOrderResult diffOrderResult, DiffOrder diffOrder) {
        if (diffOrder.getAmount().length() > 1) {
            priorityAsks.add(new Ask(
              diffOrderResult.getBook(),
              diffOrder.getOrderId(),
              diffOrder.getRate(),
              diffOrder.getAmount()
            ));
        }
    }

    String getCurrentSequence() {
        return currentSequence;
    }

    String getMinSequence() {
        return minSequence;
    }
}
