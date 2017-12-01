package service.orders.$tools.holders;

import service.model.diff_orders.DiffOrderResult;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class CurrentDiffOrdersHolder {
    private static CurrentDiffOrdersHolder currentDiffOrdersHolder;
    private final BlockingDeque<DiffOrderResult> diffOrders = new LinkedBlockingDeque<>();

    private CurrentDiffOrdersHolder() {}

    public static CurrentDiffOrdersHolder getInstance() {
        if(currentDiffOrdersHolder == null) {
            currentDiffOrdersHolder = new CurrentDiffOrdersHolder();
        }
        return currentDiffOrdersHolder;
    }

    public void produce(DiffOrderResult diffOrderResult) throws InterruptedException
    {
        diffOrders.offerFirst(diffOrderResult, 2, TimeUnit.MINUTES);
    }

    public DiffOrderResult consume() throws InterruptedException {
        return diffOrders.pollLast(2, TimeUnit.MINUTES);
    }

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
