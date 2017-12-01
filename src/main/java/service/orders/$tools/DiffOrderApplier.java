package service.orders.$tools;

import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;
import service.orders.$tools.holders.OrderBookHolder;
import service.orders.$tools.rest_client.OrderBookRestApiClient;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DiffOrderApplier {
    private static DiffOrderApplier diffOrderApplier;
    private final CurrentDiffOrdersHolder currentDiffOrdersHolder;
    private final OrderBookHolder orderBookHolder;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final Runnable applyDiffOrdersRunnable = this::applyDiffOrders;

    private DiffOrderApplier(
      CurrentDiffOrdersHolder currentDiffOrdersHolder,
      OrderBookHolder orderBookHolder,
      ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        this.currentDiffOrdersHolder = currentDiffOrdersHolder;
        this.orderBookHolder = orderBookHolder;
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
    }

    static DiffOrderApplier getInstance(
      CurrentDiffOrdersHolder currentDiffOrdersHolder,
      OrderBookHolder orderBookHolder,
      ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        if (diffOrderApplier == null) {
            diffOrderApplier = new DiffOrderApplier(
              currentDiffOrdersHolder,
              orderBookHolder,
              scheduledThreadPoolExecutor);
        }
        return diffOrderApplier;
    }

    public static DiffOrderApplier getInstance() {
        return getInstance(
          CurrentDiffOrdersHolder.getInstance(),
          OrderBookHolder.getInstance(new OrderBookRestApiClient()),
          new ScheduledThreadPoolExecutor(1));
    }

    public void start() {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(applyDiffOrdersRunnable, 1, 1, TimeUnit.SECONDS);
    }

    void applyDiffOrders() {
        try {
            DiffOrderResult diffOrderResult = currentDiffOrdersHolder.consume();
            orderBookHolder.applyDiffOrder(diffOrderResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void clearInstance() {
        diffOrderApplier = null;
        System.out.println("DiffOrderApplier instance was cleared!");
    }

    Runnable getApplyDiffOrdersRunnable() {
        return applyDiffOrdersRunnable;
    }

    public void stop() {
        scheduledThreadPoolExecutor.shutdown();
    }
}
