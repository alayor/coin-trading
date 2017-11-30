package service.orders.$tools.holders;

import service.model.diff_orders.DiffOrderResult;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class CurrentDiffOrdersHolder {
    private static final CurrentDiffOrdersHolder currentDiffOrdersHolder = new CurrentDiffOrdersHolder();
    private final BlockingDeque<DiffOrderResult> diffOrders = new LinkedBlockingDeque<>();

    private CurrentDiffOrdersHolder() {}

    public static CurrentDiffOrdersHolder getInstance() {
        return currentDiffOrdersHolder;
    }

    public void produce(DiffOrderResult diffOrderResult) throws InterruptedException
    {
        diffOrders.offerFirst(diffOrderResult, 20, TimeUnit.SECONDS);
    }

    public DiffOrderResult consume() throws InterruptedException {
        return diffOrders.pollLast(20, TimeUnit.SECONDS);
    }

    public DiffOrderResult getNext(int timeout, TimeUnit timeUnit) throws InterruptedException {
        return diffOrders.pollLast(timeout, timeUnit);
    }

    public void clear() {
        diffOrders.clear();
    }
}
