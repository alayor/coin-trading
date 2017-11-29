package service.orders.tools;

import service.model.DiffOrderResult;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class CurrentDiffOrdersHolder {
    private final BlockingDeque<DiffOrderResult> diffOrders = new LinkedBlockingDeque<>(500);

    public void produce(DiffOrderResult diffOrderResult) {
        diffOrders.offerFirst(diffOrderResult);
    }

    public DiffOrderResult consume() throws InterruptedException {
        return diffOrders.takeLast();
    }
}
