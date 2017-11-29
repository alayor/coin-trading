package service.orders.tools;

import service.model.diff_orders.DiffOrderResult;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class CurrentDiffOrdersHolder {
    private static final CurrentDiffOrdersHolder currentDiffOrdersHolder = new CurrentDiffOrdersHolder();
    private final BlockingDeque<DiffOrderResult> diffOrders = new LinkedBlockingDeque<>(500);

    private CurrentDiffOrdersHolder() {}

    public static CurrentDiffOrdersHolder getInstance() {
        return currentDiffOrdersHolder;
    }

    void produce(DiffOrderResult diffOrderResult) {
        diffOrders.offerFirst(diffOrderResult);
    }

    public DiffOrderResult consume() throws InterruptedException {
        return diffOrders.takeLast();
    }
}
