package service.orders.$tools.holders;

import service.model.diff_orders.DiffOrder;
import service.model.diff_orders.DiffOrderResult;
import service.model.orders.Ask;
import service.model.orders.Bid;
import service.model.orders.OrderBookResult;
import service.orders.$tools.rest_client.OrderBookRestApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Integer.parseInt;

/**
 * Contains the current Order Book.
 * It also contains a data structure that keeps the best bids and best asks
 * from the current order book.
 */
public class OrderBookHolder {
    private static OrderBookHolder orderBookHolder;
    private final OrderBookRestApiClient orderBookApiClient;
    private String minSequence = "";
    private String currentSequence = "";
    private Set<String> currentOrderIds = ConcurrentHashMap.newKeySet();
    private BlockingQueue<Bid> topBids = new PriorityBlockingQueue<>(1000);
    private BlockingQueue<Ask> topAsks = new PriorityBlockingQueue<>(1000);

    private OrderBookHolder(OrderBookRestApiClient orderBookApiClient) {
        this.orderBookApiClient = orderBookApiClient;
    }

    /**
     * Creates and retrieves the an instance of this class using the specified api client.
     * @param orderBookApiClient is the api client to be used to retrieve the current order book.
     * @return a new or the current instance.
     */
    public static OrderBookHolder getInstance(OrderBookRestApiClient orderBookApiClient) {
        if (orderBookHolder == null) {
            orderBookHolder = new OrderBookHolder(orderBookApiClient);
        }
        return orderBookHolder;
    }

    /**
     * Returns a new instance using the default OrderBookRestApiClient.
     * @return a new or the current instance.
     */
    public static OrderBookHolder getInstance() {
        return getInstance(new OrderBookRestApiClient());
    }

    /**
     * It retrieves the order book from the api client and loads the current bids ans asks
     * queues.
     * It also updates the current applied sequence.
     */
    public void loadOrderBook() {
        OrderBookResult orderBookResult = orderBookApiClient.getOrderBook();
        if (orderBookResult != null) {
            clear();
            this.currentSequence = orderBookResult.getOrderBook().getSequence();
            this.minSequence = orderBookResult.getOrderBook().getSequence();
            loadAsks(orderBookResult.getOrderBook().getAsks());
            loadBids(orderBookResult.getOrderBook().getBids());
        } else {
            throw new RuntimeException("Order Book could not get loaded.");
        }
    }

    /**
     * Updates the current bids and asks order queues using the information from
     * the new diff orders received from the Web Socket.
     * It may add, remove, or update current bids and asks orders.
     * @param diffOrderResult contains the new diff-orders to be applied to the current order book.
     */
    public void applyDiffOrder(DiffOrderResult diffOrderResult) {
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            if (parseInt(diffOrderResult.getSequence()) >= parseInt(this.minSequence)) {
                if (parseInt(diffOrderResult.getSequence()) == parseInt(this.currentSequence) + 1) {
                    this.currentSequence = diffOrderResult.getSequence();
                    applyToBidsOrAsks(diffOrderResult);
                } else {
                    loadOrderBook();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the lowest price ask orders.
     * @param limit is the maximum number of ask orders to be returned.
     * @return the lowest price ask orders ordered by price asc.
     */
    public List<Ask> getBestAsks(int limit) {
        List<Ask> bestAsks = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            Ask ask = topAsks.poll();
            if (ask != null) {
                bestAsks.add(ask);
            }
        }
        topAsks.addAll(bestAsks);
        return bestAsks;
    }

    /**
     * Returns the highest price buy orders.
     * @param limit is the maximum number of buy orders to be returned.
     * @return the highest price buy orders ordered by price desc.
     */
    public List<Bid> getBestBids(int limit) {
        List<Bid> bestBids = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            Bid bid = topBids.poll();
            if (bid != null) {
                bestBids.add(bid);
            }
        }
        topBids.addAll(bestBids);
        return bestBids;
    }

    private void loadAsks(List<Ask> asks) {
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            asks.stream().map(Ask::getOrderId).forEach(id -> currentOrderIds.add(id));
            topAsks.addAll(asks);
        } finally {
            lock.unlock();
        }
    }

    private void loadBids(List<Bid> bids) {
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            bids.stream().map(Bid::getOrderId).forEach(id -> currentOrderIds.add(id));
            topBids.addAll(bids);
        } finally {
            lock.unlock();
        }
    }

    void clear() {
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            currentOrderIds.clear();
            topBids.clear();
            topAsks.clear();
            currentSequence = "";
            minSequence = "";
        } finally {
            lock.unlock();
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
        if (shouldRemove(diffOrder)) {
            removeOrderFromBids(diffOrderResult, diffOrder);
            currentOrderIds.remove(diffOrder.getOrderId());
        } else if (shouldUpdate(diffOrder)) {
            removeOrderFromBids(diffOrderResult, diffOrder);
            addOrderToBids(diffOrderResult, diffOrder);
        } else if (shouldAdd(diffOrder)) {
            addOrderToBids(diffOrderResult, diffOrder);
            currentOrderIds.add(diffOrder.getOrderId());
        }
    }

    private boolean shouldRemove(DiffOrder diffOrder) {
        return diffOrder.getAmount().isEmpty() && currentOrderIds.contains(diffOrder.getOrderId());
    }

    private boolean shouldUpdate(DiffOrder diffOrder) {
        return !diffOrder.getAmount().isEmpty() && currentOrderIds.contains(diffOrder.getOrderId());
    }

    private boolean shouldAdd(DiffOrder diffOrder) {
        return !diffOrder.getAmount().isEmpty();
    }

    private void removeOrderFromBids(DiffOrderResult diffOrderResult, DiffOrder diffOrder) {
        topBids.remove(new Bid(
          diffOrderResult.getBook(),
          diffOrder.getOrderId(),
          diffOrder.getRate(),
          diffOrder.getAmount()
        ));
    }

    private void addOrderToBids(DiffOrderResult diffOrderResult, DiffOrder diffOrder) {
        topBids.add(new Bid(
          diffOrderResult.getBook(),
          diffOrder.getOrderId(),
          diffOrder.getRate(),
          diffOrder.getAmount()
        ));
    }

    private void applyOrderToAsks(DiffOrderResult diffOrderResult, DiffOrder diffOrder) {
        if (shouldRemove(diffOrder)) {
            removeOrderFromAsks(diffOrderResult, diffOrder);
            currentOrderIds.remove(diffOrder.getOrderId());
        } else if (shouldUpdate(diffOrder)) {
            removeOrderFromAsks(diffOrderResult, diffOrder);
            addOrderToAsks(diffOrderResult, diffOrder);
        } else if (shouldAdd(diffOrder)) {
            addOrderToAsks(diffOrderResult, diffOrder);
            currentOrderIds.add(diffOrder.getOrderId());
        }
    }

    private void removeOrderFromAsks(DiffOrderResult diffOrderResult, DiffOrder diffOrder) {
        topAsks.remove(new Ask(
          diffOrderResult.getBook(),
          diffOrder.getOrderId(),
          diffOrder.getRate(),
          diffOrder.getAmount()
        ));
    }

    private void addOrderToAsks(DiffOrderResult diffOrderResult, DiffOrder diffOrder) {
        topAsks.add(new Ask(
          diffOrderResult.getBook(),
          diffOrder.getOrderId(),
          diffOrder.getRate(),
          diffOrder.getAmount()
        ));
    }

    String getCurrentSequence() {
        return currentSequence;
    }

    String getMinSequence() {
        return minSequence;
    }

    Set<String> getCurrentOrderIds() {
        return currentOrderIds;
    }

    static void clearInstance() {
        orderBookHolder = null;
        System.out.println("OrderBookHolder instance was cleared!");
    }
}
