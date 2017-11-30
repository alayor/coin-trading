package service.orders.$tools;

import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;
import service.orders.$tools.holders.OrderBookHolder;

public class DiffOrderApplier {
    private static DiffOrderApplier diffOrderApplier;
    private final CurrentDiffOrdersHolder currentDiffOrdersHolder;
    private final OrderBookHolder orderBookHolder;

    private DiffOrderApplier(CurrentDiffOrdersHolder currentDiffOrdersHolder, OrderBookHolder orderBookHolder) {
        this.currentDiffOrdersHolder = currentDiffOrdersHolder;
        this.orderBookHolder = orderBookHolder;
    }

    public void start() {

    }

    public static DiffOrderApplier getInstance(
            CurrentDiffOrdersHolder currentDiffOrdersHolder,
            OrderBookHolder orderBookHolder) {
        if(diffOrderApplier == null) {
            diffOrderApplier = new DiffOrderApplier(currentDiffOrdersHolder, orderBookHolder);
        }
        return diffOrderApplier;
    }

    public static DiffOrderApplier getInstance() {
        return getInstance(CurrentDiffOrdersHolder.getInstance(), OrderBookHolder.getInstance());
    }

    void applyDiffOrders() throws InterruptedException {
        DiffOrderResult diffOrderResult = currentDiffOrdersHolder.consume();
        orderBookHolder.applyDiffOrder(diffOrderResult);
    }

    public static void clearInstance() {
        diffOrderApplier = null;
        System.out.println("DiffOrderApplier instance was cleared!");
    }
}
