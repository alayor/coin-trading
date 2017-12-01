package service.orders.$tools.holders;

import service.model.diff_orders.DiffOrderResult;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Holds the current diff-orders retrieved from the Web Socket.
 */
public class CurrentDiffOrdersHolder {
    private static CurrentDiffOrdersHolder currentDiffOrdersHolder;
    private final BlockingDeque<DiffOrderResult> diffOrders = new LinkedBlockingDeque<>();

    private CurrentDiffOrdersHolder() {
    }

    /**
     * Creates an instance if has not been created and returns it.
     *
     * @return an instance of this class.
     */
    public static CurrentDiffOrdersHolder getInstance() {
        if (currentDiffOrdersHolder == null) {
            currentDiffOrdersHolder = new CurrentDiffOrdersHolder();
        }
        return currentDiffOrdersHolder;
    }

    /**
     * Enqueue a new diff order to this holder. It may wait until 2 minutes for
     * a new space in case the queue is full.
     *
     * @param diffOrderResult the new diff order to be enqueue.
     * @throws InterruptedException if the waiting process is interrupted by another thread.
     */
    public void produce(DiffOrderResult diffOrderResult) throws InterruptedException {
        diffOrders.offerFirst(diffOrderResult, 2, TimeUnit.MINUTES);
    }

    /**
     * Dequeue a new diff order from this holder. It may wait until 2 minutes for
     * a new diff order to be added in case the queue is empty.
     * @throws InterruptedException if the waiting process is interrupted by another thread.
     */
    public DiffOrderResult consume() throws InterruptedException {
        return diffOrders.pollLast(2, TimeUnit.MINUTES);
    }

    /**
     * It should only be used by tests.
     */
    public DiffOrderResult getNext(int timeout, TimeUnit timeUnit) throws InterruptedException {
        return diffOrders.pollLast(timeout, timeUnit);
    }

    public void clear() {
        diffOrders.clear();
    }

    static void clearInstance() {
        currentDiffOrdersHolder = null;
    }
}
